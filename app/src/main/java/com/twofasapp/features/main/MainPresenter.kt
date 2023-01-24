package com.twofasapp.features.main

import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.extensions.doNothing
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.permissions.CameraPermissionRequest
import com.twofasapp.permissions.PermissionStatus
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.resources.R
import com.twofasapp.services.domain.ConvertOtpLinkToService
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.start.domain.DeeplinkHandler
import com.twofasapp.usecases.backup.CurrentBackupSchema
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.model.SyncStatus
import com.twofasapp.usecases.rateapp.RateAppCondition
import com.twofasapp.usecases.rateapp.UpdateRateAppStatus
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.CheckServiceExists
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.usecases.services.ServicesRefreshTrigger
import com.twofasapp.usecases.totp.ParseOtpAuthLink
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val view: MainContract.View,
    private val navigator: ScopedNavigator,
    private val cameraPermissionRequest: CameraPermissionRequest,
    private val getServices: GetServices,
    private val updateRateAppStatus: UpdateRateAppStatus,
    private val drawerPresenter: DrawerPresenter,
    private val toolbarPresenter: ToolbarPresenter,
    private val storeGroups: StoreGroups,
    private val storeHotpServices: StoreHotpServices,
    private val syncSyncBackupDispatcher: SyncBackupWorkDispatcher,
    private val servicesRefreshTrigger: ServicesRefreshTrigger,
    private val addService: AddService,
    private val appBuild: AppBuild,
    private val parseOtpAuthLink: ParseOtpAuthLink,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
    private val deeplinkHandler: DeeplinkHandler,
    private val rateAppCondition: RateAppCondition,
    private val analyticsService: AnalyticsService,
    private val currentBackupSchema: CurrentBackupSchema,
    private val checkServiceExists: CheckServiceExists,
    private val servicesOrderPreference: com.twofasapp.prefs.usecase.ServicesOrderPreference,
    private val appUpdateLastCheckVersionPreference: com.twofasapp.prefs.usecase.AppUpdateLastCheckVersionPreference,
    private val observeSyncStatus: ObserveSyncStatus,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) : MainContract.Presenter() {

    private val watchDisposables = CompositeDisposable()
    private val isBackupActive: Boolean
        get() = remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE

    override fun onViewAttached() {
        storeHotpServices.clear()
        drawerPresenter.onViewAttached()
        toolbarPresenter.onViewAttached()
        updateRateAppStatus.incrementCounter()

        when {
            rateAppCondition.execute() -> view.showRateApp()
        }

        currentBackupSchema.observe()
            .safelySubscribe { latestVersion ->
                if (com.twofasapp.prefs.model.RemoteBackup.CURRENT_SCHEMA < latestVersion && currentBackupSchema.isNoticeDisplayed()
                        .not()
                ) {
                    currentBackupSchema.markNoticeDisplayed()
                    view.showUpgradeAppNoticeDialog { navigator.openGooglePlay() }
                }
            }

        observeSyncStatus.observe()
            .safelySubscribe {
                when (it) {
                    is SyncStatus.Error -> view.setSyncFabVisible(true)
                    else -> view.setSyncFabVisible(isBackupActive.not())
                }
            }
    }

    override fun onViewDetached() {
        drawerPresenter.onViewDetached()
        toolbarPresenter.onViewDetached()
        super.onViewDetached()
    }

    override fun onResume() {
        deeplinkHandler.getQueuedDeeplink()?.let { handleIncomingDeeplink(it) }

        view.setSyncFabVisible(isBackupActive.not())
    }

    override fun startObservingPushes() {
    }

    override fun stopObservingPushes() {
        watchDisposables.clear()
    }

    override fun markAppUpdateDisplayed() {
        appUpdateLastCheckVersionPreference.put(appBuild.versionCode.toLong())
    }

    override fun updateUnreadNotifications(hasUnreadNotifications: Boolean) {
        drawerPresenter.updateUnreadNotifications(hasUnreadNotifications)
        toolbarPresenter.updateUnreadNotifications(hasUnreadNotifications)
    }

    override fun onAddServiceManuallyClick() {
        navigator.openShowService(
            ServiceDto(
                id = 0,
                name = "",
                secret = "",
                authType = ServiceDto.AuthType.TOTP,
                assignedDomains = emptyList(),
                serviceTypeId = null,
                iconCollectionId = ServiceIcons.defaultCollectionId,
                source = ServiceDto.Source.Manual,
            )
        )
    }

    override fun onAddServiceQrClick() {
        cameraPermissionRequest.execute()
            .subscribe(this::onCameraPermissionResult).addToDisposables()
    }

    override fun onAddGroupClick() {
        view.showCreateGroupDialog {
            val group = com.twofasapp.prefs.model.Group.generateNew(it)
            storeGroups.add(group)
            servicesRefreshTrigger.trigger()
            syncSyncBackupDispatcher.dispatch(SyncBackupTrigger.GROUPS_CHANGED)
            analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.GROUP_ADD)
        }
    }

    override fun onSortClick() {
        val order = servicesOrderPreference.get()

        view.showSortDialog(
            com.twofasapp.prefs.model.ServicesOrder.Type.values().indexOf(order.type)
        ) {
            servicesOrderPreference.put(
                order.copy(type = com.twofasapp.prefs.model.ServicesOrder.Type.values()[it])
            )
            servicesRefreshTrigger.trigger()
        }
    }

    private fun onCameraPermissionResult(permissionStatus: PermissionStatus) {
        when (permissionStatus) {
            PermissionStatus.GRANTED -> navigator.openAddServiceQr()
            PermissionStatus.DENIED -> {
            }

            PermissionStatus.DENIED_NEVER_ASK -> view.showRationaleDialog(
                R.string.permissions__camera_permission,
                R.string.permissions__camera_permission_description
            )
        }
    }

    private fun handleIncomingDeeplink(incomingData: String) {
        parseOtpAuthLink.execute(ParseOtpAuthLink.Params(incomingData))
            .flatMap { checkServiceExists.execute(it.secret) }
            .safelySubscribe { isExists ->
                if (isExists) {
                    view.showServiceExistsDialog(
                        confirmAction = { saveService(incomingData) },
                        cancelAction = { doNothing() }
                    )
                } else {
                    saveService(incomingData)
                }
            }
    }

    private fun saveService(incomingData: String) {
        parseOtpAuthLink.execute(ParseOtpAuthLink.Params(incomingData))
            .map { convertOtpLinkToService.execute(it) }
            .flatMapCompletable { addService.execute(AddService.Params(it)) }
            .andThen(getServices.execute())
            .subscribe({ showServiceBottomSheet(it.last(), false) }, {})
            .addToDisposables()
    }

    override fun showSnackbarShort(message: Int) {
        view.showSnackbarShort(message)
    }

    override fun showServiceBottomSheet(serviceDto: ServiceDto, isFromGallery: Boolean) {
        if (isFromGallery) {
            view.showRemoveQrReminder(serviceDto)
        } else {
            view.showServiceBottomSheet(serviceDto)
            servicesRefreshTrigger.trigger()
        }
    }

    override fun onReviewSuccess() {
        updateRateAppStatus.markShown()
    }

    override fun onReviewFailed(exception: Exception?) {
        analyticsService.captureException(exception)
    }

    override fun canDisplayAppUpdate(): Boolean {
        return appBuild.versionCode.toLong() != appUpdateLastCheckVersionPreference.get()
                && rateAppCondition.execute().not()
    }
}
