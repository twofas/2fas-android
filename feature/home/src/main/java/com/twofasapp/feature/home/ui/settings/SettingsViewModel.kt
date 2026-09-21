package com.twofasapp.feature.home.ui.settings

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.domain.CloudSyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SettingsViewModel(
    private val backupRepository: BackupRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())

    init {
        launchScoped {
            backupRepository.observeCloudSyncStatus().collect { cloudSyncStatus ->
                uiState.update {
                    it.copy(
                        showBackupError = cloudSyncStatus is CloudSyncStatus.Error && cloudSyncStatus.shouldShowError(),
                    )
                }
            }
        }
    }
}