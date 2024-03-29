package com.twofasapp.feature.backup.ui.backupsettings

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.domain.CloudSyncTrigger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BackupSettingsViewModel(
    private val backupRepository: BackupRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupSettingsUiState> = MutableStateFlow(BackupSettingsUiState())

    init {
        launchScoped {
            backupRepository.observeCloudBackupStatus().collect { cloudBackupStatus ->
                uiState.update {
                    it.copy(
                        syncActive = cloudBackupStatus.active,
                        account = cloudBackupStatus.account.orEmpty(),
                        encrypted = cloudBackupStatus.encrypted,
                        lastSyncMillis = cloudBackupStatus.lastSyncMillis,
                    )
                }
            }
        }

        launchScoped {
            backupRepository.observeCloudSyncStatus().collect { syncStatus ->
                uiState.update {
                    it.copy(syncStatus = syncStatus)
                }
            }
        }

        launchScoped {
            backupRepository.observePasswordForCloudSync().collect { pass ->
                uiState.update { it.copy(pass = pass) }
            }
        }
    }

    fun setPassword(password: String) {
        if (uiState.value.syncActive) {
            backupRepository.dispatchCloudSync(CloudSyncTrigger.SetPassword, password)
        } else {
            backupRepository.setPasswordForCloudSync(password)
        }
    }

    fun removePassword(password: String) {
        launchScoped {
            if (uiState.value.syncActive.not()) {
                if (password == uiState.value.pass) {
                    backupRepository.setPasswordForCloudSync(null)
                } else {
                    publishEvent(BackupSettingsUiEvent.ShowRemovePasswordDialogError)
                }
                return@launchScoped
            }

            val isCorrect = backupRepository.checkCloudBackupPassword(password)

            if (isCorrect) {
                backupRepository.dispatchCloudSync(CloudSyncTrigger.RemovePassword, password)
            } else {
                publishEvent(BackupSettingsUiEvent.ShowRemovePasswordDialogError)
            }
        }
    }

    fun deleteBackup(password: String?) {
        launchScoped {
            if (uiState.value.encrypted) {
                val isCorrect = backupRepository.checkCloudBackupPassword(password)

                if (isCorrect) {
                    backupRepository.dispatchWipeData()
                    publishEvent(BackupSettingsUiEvent.Finish)
                } else {
                    publishEvent(BackupSettingsUiEvent.ShowWipePasswordDialogError)
                }
            } else {
                backupRepository.dispatchWipeData()
                publishEvent(BackupSettingsUiEvent.Finish)
            }
        }
    }

    fun consumeEvent(event: BackupSettingsUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: BackupSettingsUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }


}