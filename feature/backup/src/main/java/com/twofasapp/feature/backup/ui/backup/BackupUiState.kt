package com.twofasapp.feature.backup.ui.backup

import android.content.Intent
import com.twofasapp.data.services.domain.CloudSyncError
import com.twofasapp.data.services.domain.CloudSyncStatus

internal data class BackupUiState(
    val syncChecked: Boolean = false,
    val syncEnabled: Boolean = true,
    val showSyncMsg: Boolean = true,
    val showError: Boolean = false,
    val error: CloudSyncError? = null,
    val exportEnabled: Boolean = false,
    val cloudSyncStatus: CloudSyncStatus = CloudSyncStatus.Default,
    val events: List<BackupUiEvent> = emptyList(),
)

internal sealed interface BackupUiEvent {
    class SignInToGoogle(val signInIntent: Intent) : BackupUiEvent
    data object SignInPermissionError : BackupUiEvent
    data object SignInNetworkError : BackupUiEvent
    data class SignInUnknownError(val msg: String?) : BackupUiEvent
    data object ShowPasswordDialogError : BackupUiEvent
}
