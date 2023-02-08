package com.twofasapp.features.main

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.browserextension.domain.FetchTokenRequestsCase
import com.twofasapp.browserextension.domain.ObserveMobileDeviceCase
import com.twofasapp.browserextension.notification.BrowserExtensionRequestPayload
import com.twofasapp.browserextension.notification.BrowserExtensionRequestReceiver
import com.twofasapp.browserextension.notification.DomainMatcher
import com.twofasapp.browserextension.ui.request.BrowserExtensionRequestActivity
import com.twofasapp.databinding.ActivityMainBinding
import com.twofasapp.design.dialogs.CancelAction
import com.twofasapp.design.dialogs.ConfirmAction
import com.twofasapp.design.dialogs.ConfirmDialog
import com.twofasapp.design.dialogs.SimpleInputDialog
import com.twofasapp.extensions.consume
import com.twofasapp.extensions.doNothing
import com.twofasapp.extensions.isTablet
import com.twofasapp.extensions.isTabletLandscape
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.extensions.startActivity
import com.twofasapp.extensions.toastLong
import com.twofasapp.extensions.visible
import com.twofasapp.features.addserviceqr.AddServiceQrActivity
import com.twofasapp.features.addserviceqr.ScanInfoDialog
import com.twofasapp.features.backup.BackupActivity
import com.twofasapp.features.services.addedservice.AddedServiceBottomSheet
import com.twofasapp.notifications.domain.FetchNotificationsCase
import com.twofasapp.notifications.domain.HasUnreadNotificationsCase
import com.twofasapp.permissions.RationaleDialog
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.resources.R
import com.twofasapp.security.ui.security.SecurityActivity
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.ui.ServiceActivity
import com.twofasapp.usecases.services.EditStateObserver
import com.twofasapp.usecases.services.SearchStateObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans
import org.koin.android.ext.android.inject

class MainServicesActivity : BaseActivityPresenter<ActivityMainBinding>(), MainContract.View, AuthAware {

    companion object {
        private const val UPDATE_REQUEST_CODE = 43513
        const val REQUEST_KEY_OPEN_SECURITY = 5689
    }

    private val presenter: MainContract.Presenter by injectThis()
    private val editStateObserver: EditStateObserver by inject()
    private val searchStateObserver: SearchStateObserver by inject()
    private val hasUnreadNotificationsCase: HasUnreadNotificationsCase by inject()
    private val fetchNotificationsCase: FetchNotificationsCase by inject()
    private val fetchTokenRequestsCase: FetchTokenRequestsCase by inject()
    private val observeMobileDeviceCase: ObserveMobileDeviceCase by inject()
    private val getServicesCase: GetServicesCase by inject()
    private val authenticationDialogs = mutableMapOf<String, MaterialDialog>()
    private val fabMenuDelegate: FabMenuDelegate by lazy { FabMenuDelegate(this, viewBinding) }
    private val rationaleDialog: RationaleDialog by lazy { RationaleDialog(this) }
    private val removeQrReminderDialog: ScanInfoDialog by lazy { ScanInfoDialog(this) }
    private lateinit var editMenuItem: MenuItem
    private lateinit var addGroupMenuItem: MenuItem
    private lateinit var sortMenuItem: MenuItem
    private lateinit var searchMenuItem: MenuItem
    private lateinit var searchView: SearchView
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val appUpdateListener: InstallStateUpdatedListener by lazy {
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackbarForCompleteUpdate()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding::inflate)
        setPresenter(presenter)
        adjustDrawerNavigationExpandPosition()
        initMenu()

        viewBinding.drawerNavigation.binding.discord.setOnClickListener {
            openBrowserApp(url = "https://discord.gg/q4cP6qh2g5")
            closeDrawer(200)
        }
        viewBinding.drawerNavigation.binding.twitter.setOnClickListener {
            openBrowserApp(url = "https://twitter.com/2fas_com")
            closeDrawer(200)
        }
        viewBinding.drawerNavigation.binding.youtube.setOnClickListener {
            openBrowserApp(url = "https://www.youtube.com/c/2FASApp")
            closeDrawer(200)
        }

        fabMenuDelegate.init(
            onAddManuallyClick = { presenter.onAddServiceManuallyClick() },
            onAddQrClick = { presenter.onAddServiceQrClick() },
            recycler = findViewById(R.id.recycler)
        )

        viewBinding.syncFab.setOnClickListener {
            startActivity<BackupActivity>()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    hasUnreadNotificationsCase().flowOn(Dispatchers.IO).collect {
                        presenter.updateUnreadNotifications(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch(Dispatchers.IO) { fetchNotificationsCase() }
                launch(Dispatchers.IO) {
                    launch(Dispatchers.IO) {

                        try {
                            val mobileDevice = observeMobileDeviceCase.invoke().first()
                            val tokenRequests = fetchTokenRequestsCase(mobileDevice.id)


                            tokenRequests.forEach { tokenRequest ->
                                val domain = DomainMatcher.extractDomain(tokenRequest.domain)
                                val matchedServices = DomainMatcher.findServicesMatchingDomain(
                                    getServicesCase(),
                                    domain
                                )

                                runOnUiThread {
                                    if (authenticationDialogs.containsKey(tokenRequest.requestId)
                                            .not()
                                    ) {
                                        authenticationDialogs.put(
                                            tokenRequest.requestId,
                                            MaterialDialog(this@MainServicesActivity)
                                                .title(text = getString(R.string.browser__2fa_token_request_title))
                                                .message(text = getString(R.string.browser__2fa_token_request_content).plus(" ${tokenRequest.domain}?"))
                                                .cancelable(false)
                                                .positiveButton(text = getString(R.string.extension__approve)) {
                                                    val isOneDomainMatched =
                                                        matchedServices.size == 1
                                                    val serviceId =
                                                        if (matchedServices.size == 1) matchedServices.first().id else null

                                                    if (isOneDomainMatched) {
                                                        val payload =
                                                            BrowserExtensionRequestPayload(
                                                                action = BrowserExtensionRequestPayload.Action.Approve,
                                                                notificationId = -1,
                                                                extensionId = tokenRequest.extensionId,
                                                                requestId = tokenRequest.requestId,
                                                                serviceId = serviceId ?: -1,
                                                                domain = domain,
                                                            )
                                                        sendBroadcast(
                                                            BrowserExtensionRequestReceiver.createIntent(
                                                                this@MainServicesActivity,
                                                                payload
                                                            )
                                                        )
                                                    } else {

                                                        val contentIntent = Intent(
                                                            this@MainServicesActivity,
                                                            BrowserExtensionRequestActivity::class.java
                                                        ).apply {
                                                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                                                            putExtra(
                                                                BrowserExtensionRequestPayload.Key,
                                                                BrowserExtensionRequestPayload(
                                                                    action = BrowserExtensionRequestPayload.Action.Approve,
                                                                    notificationId = -1,
                                                                    extensionId = tokenRequest.extensionId,
                                                                    requestId = tokenRequest.requestId,
                                                                    serviceId = serviceId ?: -1,
                                                                    domain = domain,
                                                                )
                                                            )
                                                        }

                                                        startActivity(contentIntent)
                                                    }
                                                }
                                                .negativeButton(text = getString(R.string.extension__deny)) {
                                                    val payload = BrowserExtensionRequestPayload(
                                                        action = BrowserExtensionRequestPayload.Action.Deny,
                                                        notificationId = -1,
                                                        extensionId = tokenRequest.extensionId,
                                                        requestId = tokenRequest.requestId,
                                                        serviceId = -1,
                                                        domain = domain,
                                                    )
                                                    sendBroadcast(
                                                        BrowserExtensionRequestReceiver.createIntent(
                                                            this@MainServicesActivity,
                                                            payload
                                                        )
                                                    )
                                                }
                                                .show { }
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun initMenu() {
        editMenuItem = viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_edit)
        editMenuItem.setOnMenuItemClickListener { consume { editStateObserver.offer(true) } }
        addGroupMenuItem = viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_add_group)
        addGroupMenuItem.setOnMenuItemClickListener { consume { presenter.onAddGroupClick() } }
        sortMenuItem = viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_sort)
        sortMenuItem.setOnMenuItemClickListener { consume { presenter.onSortClick() } }
        searchMenuItem = viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_search)
        searchView =
            viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_search).actionView!!.findViewById(com.twofasapp.R.id.searchView)
        searchView.maxWidth = Integer.MAX_VALUE;
        searchView.queryTextChanges().doOnNext { searchStateObserver.offer(it.toString()) }
            .subscribe()
        searchView.setOnSearchClickListener { searchStateObserver.offer(true) }
        searchView.setOnCloseListener {
            searchStateObserver.offer(false)
            false
        }
    }

    override fun onAuthenticated() {
        presenter.startObservingPushes()
        checkAppVersionUpdate()
    }

    override fun getStringRes(res: Int): String {
        return getString(res)
    }

    override fun onPause() {
        super.onPause()
        presenter.stopObservingPushes()
        authenticationDialogs.forEach { it.value.dismiss() }
        authenticationDialogs.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        if (requestCode == ServiceActivity.REQUEST_KEY_ADD_SERVICE) {
            data?.getParcelableExtra<ServiceDto>(ServiceActivity.RESULT_SERVICE)?.let {
                presenter.showServiceBottomSheet(it, false)
            }
            return
        }

        if (requestCode == AddServiceQrActivity.REQUEST_CODE) {
            val isFromGallery =
                data?.getBooleanExtra(AddServiceQrActivity.RESULT_IS_FROM_GALLERY, false) ?: false
            data?.getParcelableExtra<ServiceDto>(AddServiceQrActivity.RESULT_SERVICE)?.let {
                presenter.showServiceBottomSheet(it, isFromGallery)
            }
            return
        }

        if (requestCode == REQUEST_KEY_OPEN_SECURITY) {
            startActivity<SecurityActivity>()
        }

        if (requestCode == UPDATE_REQUEST_CODE) {
            toastLong("Updating. Please wait...")
            return
        }
    }


    override fun showRemoveQrReminder(serviceDto: ServiceDto) {
        val desc = Spanner()
            .append(getString(R.string.tokens__gallery_advice_content_first))
            .append(getString(R.string.tokens__gallery_advice_content_middle_bold), Spans.bold())
            .append(getString(R.string.tokens__gallery_advice_content_last))

        removeQrReminderDialog.show(
            title = getString(R.string.tokens__gallery_advice_title),
            desc = "",
            descSpan = desc,
            okText = getString(R.string.commons__got_it),
            imageRes = R.drawable.remove_qr_reminder_image,
            showCancel = false,
            action = { removeQrReminderDialog.dismiss() },
            actionDismiss = { showServiceBottomSheet(serviceDto) },
        )
    }

    override fun closeDrawer(delay: Long) {
        Handler().postDelayed({ viewBinding.drawerLayout.closeDrawer(GravityCompat.START) }, delay)
    }

    override fun setSyncFabVisible(isVisible: Boolean) {
        viewBinding.syncFab.visible(isVisible)
    }

    override fun showLoginRequestDialog(title: String, content: String, authenticationId: Int) {
        val contentBuilder = SpannableStringBuilder(title)

        try {
            Regex("(?:.*)(?:from )(.*)(?: to )(.*)(?: account)(?:.*)").matchEntire(title)?.groupValues?.let {
                val browser = it.getOrNull(1)
                val account = it.getOrNull(2)

                if (browser != null && account != null) {
                    contentBuilder.setSpan(
                        StyleSpan(Typeface.BOLD),
                        title.indexOf(browser),
                        title.indexOf(browser) + browser.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    contentBuilder.setSpan(
                        StyleSpan(Typeface.BOLD),
                        title.indexOf(account),
                        title.indexOf(account) + account.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        } catch (e: Exception) {
            /* do nothing */
        }
    }

    override fun onBackPressed() {
        when {
            viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START) -> closeDrawer()
            fabMenuDelegate.isFabOpen() -> fabMenuDelegate.closeFabMenu()
            searchStateObserver.isSearchEnabled -> searchStateObserver.offer(false)
            editStateObserver.isEditEnabled -> editStateObserver.offer(false)
            else -> super.onBackPressed()
        }
    }

    override fun openDrawer() = viewBinding.drawerLayout.openDrawer(GravityCompat.START)

    override fun getStringFromRes(resId: Int): String = getString(resId)

    override fun showRationaleDialog(title: Int, content: Int) =
        rationaleDialog.show(title, content)

    override fun showSnackbarLong(msgRes: Int) {
        Snackbar.make(viewBinding.coordinator, msgRes, Snackbar.LENGTH_LONG).show()
    }

    override fun showSnackbarShort(message: Int) {
        Snackbar.make(viewBinding.coordinator, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showServiceBottomSheet(serviceDto: ServiceDto) {
        AddedServiceBottomSheet.newInstance(serviceDto)
            .show(supportFragmentManager, AddedServiceBottomSheet::javaClass.name)
    }

    override fun setDrawerItems(entries: List<DrawerEntry>) {
        viewBinding.drawerNavigation.setItems(entries)
    }

    override fun setEditButtonVisible(isVisible: Boolean) {
        editMenuItem.isVisible = isVisible
    }

    override fun setSearchButtonVisible(isVisible: Boolean) {
        searchMenuItem.isVisible = isVisible
        searchView.isVisible = isVisible
    }

    override fun setAddGroupButtonVisible(isVisible: Boolean) {
        addGroupMenuItem.isVisible = isVisible
    }

    override fun setSortButtonVisible(isVisible: Boolean) {
        sortMenuItem.isVisible = isVisible
    }

    override fun setSearchVisible(isVisible: Boolean) {
        if (isVisible.not()) {
            searchView.onActionViewCollapsed()
        }
    }

    override fun setFabVisible(isVisible: Boolean) {
        if (isVisible) fabMenuDelegate.showFab() else fabMenuDelegate.hideFab()
    }

    override fun setToolbarState(state: ToolbarState) {
        val icon = AppCompatResources.getDrawable(this, state.iconRes)

        with(viewBinding) {
            toolbar.navigationIcon = icon
            toolbar.setNavigationOnClickListener { state.iconAction() }
            toolbar.navigationContentDescription = state.iconContentDescription
            toolbarTitle.text = state.titleRes?.let { getString(it) }
            toolbarTitle.gravity =
                if (state.isTitleCentered) Gravity.CENTER else Gravity.START or Gravity.CENTER_VERTICAL
        }
    }

    override fun showCreateGroupDialog(onSaveAction: (String) -> Unit) {
        SimpleInputDialog(this).show(
            hint = getString(R.string.tokens__group_name),
            allowEmpty = false,
            maxLength = 32,
            okAction = { onSaveAction.invoke(it) }
        )
    }

    override fun showRateApp() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val flow = manager.launchReviewFlow(this, task.result)
                flow.addOnCompleteListener {
                    presenter.onReviewSuccess()
                }
            } else {
                presenter.onReviewFailed(task.exception)
            }
        }
    }

    override fun showUpgradeAppNoticeDialog(action: () -> Unit) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.update_app_title),
            msg = getString(R.string.update_app_msg),
            positiveButtonText = "Update",
            negativeButtonText = "Later",
        ).show(
            confirmAction = { action() }
        )
    }

    override fun showSortDialog(initialSelection: Int, onSelectAction: (Int) -> Unit) {
        MaterialDialog(this)
            .title(text = getString(R.string.tokens__sort_by))
            .listItemsSingleChoice(
                R.array.services_sort_type,
                initialSelection = initialSelection
            ) { _, index, text ->
                onSelectAction(index)
            }
            .show()
    }

    override fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction) {
        ConfirmDialog(this, R.string.commons__warning, R.string.tokens__service_already_exists)
            .show(confirmAction = confirmAction, cancelAction = cancelAction)
    }

    private fun adjustDrawerNavigationExpandPosition() {
        if (isTablet() || isTabletLandscape()) {
            viewBinding.drawerNavigation.layoutParams =
                (viewBinding.drawerNavigation.layoutParams as DrawerLayout.LayoutParams).apply {
                    width = DrawerLayout.LayoutParams.WRAP_CONTENT
                }
        }
    }

    private fun showSnackbarForCompleteUpdate() {
        try {
            Snackbar.make(
                findViewById(R.id.coordinator),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction("RESTART") {
                    appUpdateManager.unregisterListener(appUpdateListener)
                    appUpdateManager.completeUpdate()
                }
                show()
            }

        } catch (e: Exception) {
            doNothing()
        }
    }

    override fun checkAppVersionUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showSnackbarForCompleteUpdate()
                }

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    && appUpdateInfo.clientVersionStalenessDays() == null
                ) {
                    if (presenter.canDisplayAppUpdate()) {
                        presenter.markAppUpdateDisplayed()
                        appUpdateManager.registerListener(appUpdateListener)
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            UPDATE_REQUEST_CODE
                        )
                    }
                }
            }
    }
}
