package com.twofasapp.features.backup.settings

import com.mikepenz.fastadapter.IItem
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.design.settings.DividerItem
import com.twofasapp.design.settings.HeaderEntry
import com.twofasapp.design.settings.SimpleEntry
import com.twofasapp.extensions.doNothing
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.resources.R
import com.twofasapp.services.workmanager.WipeGoogleDriveWorkDispatcher
import com.twofasapp.time.domain.formatter.DurationFormatter
import com.twofasapp.usecases.app.CheckConnectionStatus
import com.twofasapp.usecases.backup.ObserveSyncStatus
import com.twofasapp.usecases.backup.model.SyncStatus

internal class BackupSettingsPresenter(
    private val view: BackupSettingsContract.View,
    private val navigator: ScopedNavigator,
    private val durationFormatter: DurationFormatter,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
    private val observeSyncStatus: ObserveSyncStatus,
    private val checkConnectionStatus: CheckConnectionStatus,
    private val wipeGoogleDriveWorkDispatcher: WipeGoogleDriveWorkDispatcher,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
) : BackupSettingsContract.Presenter() {

    private var syncStatus: SyncStatus = SyncStatus.Default
    private var backupPassword: String = ""
    private var isViewAttached = false
    private val backupStatus: com.twofasapp.prefs.model.RemoteBackupStatus
        get() = remoteBackupStatusPreference.get()

    override fun onViewAttached() {
        observeSyncStatus.observe()
            .safelySubscribe {
                syncStatus = it
                updateViewState()

                when (it) {
                    SyncStatus.Default -> doNothing()
                    is SyncStatus.Synced -> {
                        if (it.trigger == SyncBackupTrigger.WIPE_DATA) {
                            wipeData()
                        }
                    }
                    SyncStatus.Syncing -> doNothing()
                    is SyncStatus.Error -> handleErrors(it.trigger)
                }

                isViewAttached = true
            }
    }

    override fun onResume() {
        updateViewState()
    }

    private fun updateViewState() {
        val items = mutableListOf<IItem<*>>()
        val isUserPasswordProtected = remoteBackupStatusPreference.get().reference.isNullOrBlank().not()

        if (isUserPasswordProtected.not()) {
            items.add(
                SimpleEntry(
                    titleRes = R.string.backup_settings_password_set_title,
                    subtitleRes = R.string.backup_settings_password_set_subtitle,
                    drawableRes = R.drawable.ic_backup_password_set,
                    isEnabled = syncStatus != SyncStatus.Syncing,
                ) {
                    view.showSetPasswordDialog()
                }.toItem()
            )
        } else {
            items.add(
                SimpleEntry(
                    titleRes = R.string.backup_settings_password_remove_title,
                    subtitleRes = R.string.backup_settings_password_remove_subtitle,
                    drawableRes = R.drawable.ic_backup_password_remove,
                    isEnabled = syncStatus != SyncStatus.Syncing,
                ) {
                    view.showRemovePasswordDialog(showError = false)
                }.toItem()
            )
        }

        items.add(
            SimpleEntry(
                titleRes = R.string.backup_settings_delete_title,
                subtitleRes = R.string.backup_settings_delete_subtitle,
                drawableRes = R.drawable.ic_backup_delete_old,
                isEnabled = syncStatus != SyncStatus.Syncing,
                clickAction = {
                    view.showWipeConfirmDialog {
                        if (checkConnectionStatus.execute().isConnected) {
                            if (isUserPasswordProtected) {
                                view.showWipePasswordDialog(showError = false)
                            } else {
                                wipeData()
                            }
                        } else {
                            view.showErrorDialog(R.string.gdrive_internet_title, R.string.gdrive_wipe_internet_msg)
                        }
                    }
                },
            ).toItem()
        )

        items.add(DividerItem())
        items.add(HeaderEntry(text = "Info").toItem())

        items.add(
            SimpleEntry(
                titleRes = R.string.backup_settings_account_title,
                subtitle = backupStatus.account,
            ).toItem()
        )
        items.add(
            SimpleEntry(
                titleRes = R.string.backup_settings_sync_title,
                subtitle = when (syncStatus) {
                    SyncStatus.Default -> view.getStringRes(R.string.backup__sync_status_waiting)
                    is SyncStatus.Synced -> {
                        if (backupStatus.lastSyncMillis == 0L) {
                            view.getStringRes(R.string.backup__sync_status_waiting)
                        } else {
                            durationFormatter.format(backupStatus.lastSyncMillis)
                        }
                    }
                    SyncStatus.Syncing -> view.getStringRes(R.string.backup__sync_status_progress)
                    is SyncStatus.Error -> {
                        when ((syncStatus as SyncStatus.Error).trigger) {
                            SyncBackupTrigger.WIPE_DATA,
                            SyncBackupTrigger.SET_PASSWORD,
                            SyncBackupTrigger.REMOVE_PASSWORD -> {
                                if (backupStatus.lastSyncMillis == 0L) {
                                    view.getStringRes(R.string.backup__sync_status_waiting)
                                } else {
                                    durationFormatter.format(backupStatus.lastSyncMillis)
                                }
                            }
                            else -> view.getStringRes(R.string.commons__error)
                        }
                    }
                }
            ).toItem()
        )

        view.updateItems(items)
    }

    override fun onPasswordDialogSaved(password: String) {
        backupPassword = password
        syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SET_PASSWORD, backupPassword)
        updateViewState()
    }

    override fun onPasswordDialogCanceled() {
        backupPassword = ""
    }

    override fun onRemovePasswordEntered(password: String) {
        syncBackupDispatcher.tryDispatch(SyncBackupTrigger.REMOVE_PASSWORD, password)
        updateViewState()
    }

    override fun onWipePasswordEntered(password: String) {
        syncBackupDispatcher.tryDispatch(SyncBackupTrigger.WIPE_DATA, password)
        updateViewState()
    }

    private fun handleErrors(trigger: SyncBackupTrigger) {
        if (isViewAttached.not()) return

        if (trigger == SyncBackupTrigger.REMOVE_PASSWORD) {
            view.showRemovePasswordDialog(showError = true)
        }

        if (trigger == SyncBackupTrigger.WIPE_DATA) {
            view.showWipePasswordDialog(showError = true)
        }
    }

    private fun wipeData() {
        remoteBackupStatusPreference.delete()
        remoteBackupKeyPreference.delete()
        wipeGoogleDriveWorkDispatcher.dispatch()
        navigator.navigateBack()
    }
}
