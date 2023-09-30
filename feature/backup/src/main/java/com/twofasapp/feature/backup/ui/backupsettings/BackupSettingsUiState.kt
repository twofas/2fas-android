package com.twofasapp.feature.backup.ui.backupsettings

import com.twofasapp.data.services.domain.CloudSyncStatus

internal data class BackupSettingsUiState(
    val syncActive: Boolean = true,
    val syncStatus: CloudSyncStatus = CloudSyncStatus.Default,
    val encrypted: Boolean = false,
    val account: String = "",
    val lastSyncMillis: Long = 0L,
    val events: List<BackupSettingsUiEvent> = emptyList(),
)

internal sealed interface BackupSettingsUiEvent {
    data object Finish : BackupSettingsUiEvent
    data object ShowWipePasswordDialogError : BackupSettingsUiEvent
    data object ShowRemovePasswordDialogError : BackupSettingsUiEvent
}
