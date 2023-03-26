package com.twofasapp.features.backup.status

import com.mikepenz.fastadapter.IItem
import com.twofasapp.BuildConfig
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.design.settings.DividerItem
import com.twofasapp.design.settings.HeaderEntry
import com.twofasapp.design.settings.SimpleEntry
import com.twofasapp.design.settings.SwitchEntry
import com.twofasapp.design.settings.SwitchEntryItem
import com.twofasapp.extensions.doNothing
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.resources.R
import com.twofasapp.services.backup.models.RemoteBackupErrorType
import com.twofasapp.services.backup.usecases.CheckRemoteBackupPassword
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.googleauth.models.GoogleAuthResult
import com.twofasapp.services.googleauth.usecases.SignInToGoogle
import com.twofasapp.services.googleauth.usecases.SignOutFromGoogle
import com.twofasapp.services.googledrive.GoogleDriveService
import com.twofasapp.services.googledrive.models.GetGoogleDriveFileResult
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.model.SyncStatus
import com.twofasapp.usecases.services.GetServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class BackupStatusPresenter(
    private val view: BackupStatusContract.View,
    private val navigator: ScopedNavigator,
    private val signInToGoogle: SignInToGoogle,
    private val signOutFromGoogle: SignOutFromGoogle,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
    private val analyticsService: AnalyticsService,
    private val observeSyncStatus: ObserveSyncStatus,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val getServices: GetServices,
    private val showBackupNotice: ShowBackupNotice,
    private val googleDriveService: GoogleDriveService,
    private val jsonSerializer: com.twofasapp.serialization.JsonSerializer,
    private val checkRemoteBackupPassword: CheckRemoteBackupPassword,
    private val sessionRepository: SessionRepository,
) : BackupStatusContract.Presenter() {

    private var syncStatus: SyncStatus = SyncStatus.Default
    private var isServicesListNotEmpty = true

    private val isBackupActive: Boolean
        get() = remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE

    private val isPasswordError: Boolean
        get() = (syncStatus as? SyncStatus.Error)?.type == RemoteBackupErrorType.DECRYPT_WRONG_PASSWORD ||
                (syncStatus as? SyncStatus.Error)?.type == RemoteBackupErrorType.DECRYPT_NO_PASSWORD


    override fun onViewAttached() {
        updateViewState()

        if (view.isOpenedFromBackupNotice() && showBackupNotice.currentValue()) {
            onSyncCheckedChanged(true)
        }

        showBackupNotice.save(false)

        observeSyncStatus.observe()
            .safelySubscribe {
                syncStatus = it
                when (it) {
                    SyncStatus.Default -> doNothing()
                    is SyncStatus.Synced -> checkServicesList()
                    SyncStatus.Syncing -> doNothing()
                    is SyncStatus.Error -> doNothing()
                }
                updateViewState()
            }
    }

    override fun onResume() {
        checkServicesList()
    }

    private fun updateViewState() {
        val items = mutableListOf<IItem<*>>()

        items.add(HeaderEntry(text = "Google Drive").toItem())
        items.add(createDriveSyncItem())
        createSyncErrorItem()?.let { items.add(it) }
        items.add(createSyncSettingsItem())

        items.add(DividerItem())
        items.add(HeaderEntry(textRes = R.string.backup__local_file_title).toItem())
        items.add(createImportItem())
        items.add(createExportItem())

        createShowBackupFileItem()?.let {
            items.add(DividerItem())
            items.add(HeaderEntry(text = "Debug").toItem())
            items.add(it)
        }

        view.updateItems(items)
    }

    private fun onSyncCheckedChanged(isChecked: Boolean) {
        when {
            isChecked.not() -> view.showTurnOffDialog {
                signOutFromGoogle.execute().safelySubscribe { doNothing() }
                analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.BACKUP_OFF)
                remoteBackupStatusPreference.put {
                    it.copy(state = RemoteBackupStatus.State.NOT_CONFIGURED, reference = null)
                }
                remoteBackupKeyPreference.delete()

                sessionRepository.resetBackupReminder()
                updateViewState()
            }

            isChecked && isPasswordError -> handleNoPasswordError(
                false,
                false,
                (syncStatus as? SyncStatus.Error)?.trigger
            )

            isChecked -> signInToGoogle.execute(navigator.requireActivity())
                .safelySubscribe { onSignInResult(it) }
        }
    }

    private fun onSignInResult(result: GoogleAuthResult) {
        when (result) {
            is GoogleAuthResult.Success -> {
                remoteBackupStatusPreference.put(
                    RemoteBackupStatus(
                        syncProvider = RemoteBackupStatus.SyncProvider.GOOGLE_DRIVE,
                        state = RemoteBackupStatus.State.ACTIVE,
                        account = result.email,
                        schemaVersion = RemoteBackup.CURRENT_SCHEMA,
                    )
                )
                syncBackupDispatcher.tryDispatch(SyncBackupTrigger.FIRST_CONNECT)
            }

            is GoogleAuthResult.Canceled -> {
                when (result.reason) {
                    GoogleAuthResult.CancelReason.PERMISSION_NOT_GRANTED ->
                        view.showErrorDialog(
                            R.string.gdrive_permission_title,
                            R.string.gdrive_permission_msg
                        )

                    GoogleAuthResult.CancelReason.NETWORK_ERROR ->
                        view.showErrorDialog(
                            R.string.gdrive_internet_title,
                            R.string.gdrive_internet_msg
                        )

                    GoogleAuthResult.CancelReason.CANCELED -> doNothing()
                }
            }

            is GoogleAuthResult.Failure -> {
                view.showErrorDialog(titleRes = R.string.commons__error, msg = result.reason.message.orEmpty())
            }
        }

        updateViewState()
    }

    private fun checkServicesList() {
        getServices.execute()
            .safelySubscribe {
                isServicesListNotEmpty = it.isNotEmpty()
                updateViewState()
            }
    }

    private fun createDriveSyncItem(): SwitchEntryItem {
        val subtitle = when {
            isBackupActive && isPasswordError -> null
            isBackupActive -> null
            else -> R.string.backup_explanation_msg
        }

        return SwitchEntry(
            titleRes = R.string.backup__drive_title,
            subtitleRes = subtitle,
            drawableRes = if (isBackupActive && isPasswordError.not()) R.drawable.ic_sync_on else R.drawable.ic_sync_off,
            drawableTintRes = R.color.accent,
            isChecked = isBackupActive && isPasswordError.not(),
            isEnabled = syncStatus != SyncStatus.Syncing,
            switchAction = { _, isChecked -> onSyncCheckedChanged(isChecked) }
        ).toItem()
    }

    private fun createSyncSettingsItem() = SimpleEntry(
        titleRes = R.string.backup__synchronization_settings,
        drawableRes = R.drawable.ic_drawer_settings,
        drawableTintRes = R.color.accent,
        isEnabled = isBackupActive && syncStatus != SyncStatus.Syncing,
        clickAction = { view.showSyncSettings() }
    ).toItem()

    private fun createShowBackupFileItem() = if (BuildConfig.DEBUG) {
        SimpleEntry(
            title = "Show Google Drive file",
            drawableRes = R.drawable.ic_drawer_developer,
            drawableTintRes = R.color.accent,
            isEnabled = isBackupActive,
            clickAction = {
                googleDriveService.getBackupFile().subscribeOn(Schedulers.io())
                    .map {
                        if (it is GetGoogleDriveFileResult.Success) {
                            jsonSerializer.serializePretty(
                                jsonSerializer.deserialize<RemoteBackup>(it.file.content),
                            )
                        } else {
                            ""
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .safelySubscribe(onError = { it.printStackTrace() }) {
                        view.showBackupFileDialog(
                            it
                        )
                    }
            }
        ).toItem()
    } else null

    private fun createImportItem() = SimpleEntry(
        titleRes = R.string.backup__import_file,
        drawableRes = R.drawable.ic_import_file_old,
        clickAction = { navigator.openImportBackup() },
    ).toItem()

    private fun createExportItem() = SimpleEntry(
        titleRes = R.string.backup__export_to_file,
        subtitleRes = R.string.backup__file_backup_offline_title,
        isEnabled = isServicesListNotEmpty,
        drawableRes = R.drawable.ic_export_file,
        clickAction = { navigator.openExportBackup() },
    ).toItem()

    private fun createSyncErrorItem() = syncStatus.let { status ->
        if (status is SyncStatus.Error
            && (isBackupActive || isPasswordError)
            && status.shouldShowError()
        ) {
            if (isPasswordError && status.trigger == SyncBackupTrigger.FIRST_CONNECT) {
                handleNoPasswordError(false, true, SyncBackupTrigger.FIRST_CONNECT)
                return null
            }

            SyncErrorItem(
                SyncErrorItem.Model(
                    type = status.type,
                    errorMsg = formatErrorMsg(status.type),
                    showErrorCode = formatShouldShowErrorCode(status.type)
                )
            )
        } else {
            null
        }
    }

    private fun formatErrorMsg(type: RemoteBackupErrorType): Int =
        when (type) {
            RemoteBackupErrorType.GOOGLE_PLAY_SERVICES_UNAVAILABLE,
            RemoteBackupErrorType.JSON_PARSING_FAILURE,
            RemoteBackupErrorType.SYNC_FAILURE,
            RemoteBackupErrorType.HTTP_API_FAILURE,
            RemoteBackupErrorType.FILE_NOT_FOUND,
            RemoteBackupErrorType.UNKNOWN -> R.string.backup_error_unknown

            RemoteBackupErrorType.NETWORK_UNAVAILABLE -> R.string.backup_error_network

            RemoteBackupErrorType.GOOGLE_USER_PERMISSION_DENIED,
            RemoteBackupErrorType.CREDENTIALS_NOT_FOUND,
            RemoteBackupErrorType.GOOGLE_AUTH_FAILURE -> R.string.backup_error_auth

            RemoteBackupErrorType.ENCRYPT_UNKNOWN_FAILURE -> R.string.backup_error_encrypt_unknown
            RemoteBackupErrorType.DECRYPT_NO_PASSWORD -> R.string.backup_error_no_password
            RemoteBackupErrorType.DECRYPT_WRONG_PASSWORD -> R.string.backup_error_wrong_password
            RemoteBackupErrorType.DECRYPT_UNKNOWN_FAILURE -> R.string.backup_error_decrypt_unknown
        }

    private fun formatShouldShowErrorCode(type: RemoteBackupErrorType) =
        when (type) {
            RemoteBackupErrorType.DECRYPT_NO_PASSWORD,
            RemoteBackupErrorType.DECRYPT_WRONG_PASSWORD -> false

            else -> true
        }

    private fun handleNoPasswordError(
        showError: Boolean,
        signOutOnCancel: Boolean = false,
        trigger: SyncBackupTrigger?
    ) {
        view.showEnterPasswordDialog(
            showError = showError,
            signOutOnCancel = signOutOnCancel
        ) { password ->

            val isCorrect = checkRemoteBackupPassword.execute(password)
            if (isCorrect.not()) handleNoPasswordError(
                true,
                signOutOnCancel,
                trigger ?: (syncStatus as? SyncStatus.Error)?.trigger
            )

            syncBackupDispatcher.tryDispatch(trigger ?: SyncBackupTrigger.FIRST_CONNECT)
        }
    }

    override fun signOut() {
        signOutFromGoogle.execute().safelySubscribe {
            remoteBackupStatusPreference.put {
                it.copy(state = RemoteBackupStatus.State.NOT_CONFIGURED, reference = null)
            }
            remoteBackupKeyPreference.delete()
            syncStatus = SyncStatus.Default
            observeSyncStatus.publish(SyncStatus.Default)
        }
    }
}
