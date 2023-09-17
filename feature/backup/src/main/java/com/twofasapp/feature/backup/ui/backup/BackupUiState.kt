package com.twofasapp.feature.backup.ui.backup

import android.content.Intent

internal data class BackupUiState(
    val syncChecked: Boolean = false,
    val exportEnabled: Boolean = false,

    val events: List<BackupUiEvent> = emptyList(),
)

internal sealed interface BackupUiEvent {
    class SignInToGoogle(val signInIntent: Intent) : BackupUiEvent
    data object SignInPermissionError : BackupUiEvent
    data object SignInNetworkError : BackupUiEvent
    data class SignInUnknownError(val msg: String?) : BackupUiEvent
}
