package com.twofasapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.extensions.doNothing
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.extensions.toastLong
import com.twofasapp.resources.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), AuthAware {

    companion object {
        private const val UPDATE_REQUEST_CODE = 43513
    }

    private val settingsRepository: SettingsRepository by inject()
    private val sessionRepository: SessionRepository by inject()
    private val servicesRepository: ServicesRepository by inject()
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdateListener: InstallStateUpdatedListener by lazy {
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackbarForCompleteUpdate()
            }
        }
    }
    private var recalculateTimeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)
        super.onCreate(savedInstanceState)
        makeWindowSecure()

        setContent { MainScreen() }

        attachAuthLifecycleObserver()
        checkAppVersionUpdate()
    }

    override fun onResume() {
        super.onResume()
        servicesRepository.setTickerEnabled(true)

        recalculateTimeJob = lifecycleScope.launch {
            servicesRepository.recalculateTimeDelta()
        }
    }

    override fun onAuthenticated() = Unit

    override fun onPause() {
        super.onPause()
        servicesRepository.setTickerEnabled(false)
        recalculateTimeJob?.cancel()
        recalculateTimeJob = null
    }

    private fun attachAuthLifecycleObserver() {
        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware
            )
        )
    }

    private fun showSnackbarForCompleteUpdate() {
        try {
            Snackbar.make(
                window.decorView.rootView,
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

    private fun checkAppVersionUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showSnackbarForCompleteUpdate()
                }

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    && appUpdateInfo.clientVersionStalenessDays() == null
                ) {
                    if (sessionRepository.showAppUpdate()) {
                        sessionRepository.setAppUpdateDisplayed()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        if (requestCode == UPDATE_REQUEST_CODE) {
            toastLong("Updating. Please wait...")
            return
        }
    }


//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode != RESULT_OK) return
//
//
//        if (requestCode == AddServiceQrActivity.REQUEST_CODE) {
//            val isFromGallery =
//                data?.getBooleanExtra(AddServiceQrActivity.RESULT_IS_FROM_GALLERY, false) ?: false
//            data?.getParcelableExtra<ServiceDto>(AddServiceQrActivity.RESULT_SERVICE)?.let {
//            }
//            return
//        }
//    }
//
//    override fun showRemoveQrReminder(serviceDto: ServiceDto) {
//        val desc = Spanner()
//            .append(getString(R.string.tokens__gallery_advice_content_first))
//            .append(getString(R.string.tokens__gallery_advice_content_middle_bold), Spans.bold())
//            .append(getString(R.string.tokens__gallery_advice_content_last))
//
//        removeQrReminderDialog.show(
//            title = getString(R.string.tokens__gallery_advice_title),
//            desc = "",
//            descSpan = desc,
//            okText = getString(R.string.commons__got_it),
//            imageRes = R.drawable.remove_qr_reminder_image,
//            showCancel = false,
//            action = { removeQrReminderDialog.dismiss() },
//            actionDismiss = { },
//        )
//    }
//
//    override fun showRateApp() {
//        val manager = ReviewManagerFactory.create(this)
//        val request = manager.requestReviewFlow()
//        request.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val flow = manager.launchReviewFlow(this, task.result)
//                flow.addOnCompleteListener {
//                    presenter.onReviewSuccess()
//                }
//            } else {
//                presenter.onReviewFailed(task.exception)
//            }
//        }
//    }
//
//    override fun showUpgradeAppNoticeDialog(action: () -> Unit) {
//        ConfirmDialog(
//            context = this,
//            title = getString(R.string.update_app_title),
//            msg = getString(R.string.update_app_msg),
//            positiveButtonText = "Update",
//            negativeButtonText = "Later",
//        ).show(
//            confirmAction = { action() }
//        )
//    }
//
//    override fun showServiceExistsDialog(confirmAction: ConfirmAction, cancelAction: CancelAction) {
//        ConfirmDialog(this, R.string.commons__warning, R.string.tokens__service_already_exists)
//            .show(confirmAction = confirmAction, cancelAction = cancelAction)
//    }
}