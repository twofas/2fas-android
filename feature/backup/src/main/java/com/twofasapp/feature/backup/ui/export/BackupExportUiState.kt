package com.twofasapp.feature.backup.ui.export

internal data class BackupExportUiState(
    val passwordChecked: Boolean = true,
    val password: String = "",
    val events: List<BackupExportUiEvent> = emptyList(),
)

internal sealed interface BackupExportUiEvent {
    data class ShowSharePicker(
        val appId: String,
        val content: String,
    ) : BackupExportUiEvent

    data object ShareError : BackupExportUiEvent
    data object DownloadError : BackupExportUiEvent
    data object DownloadSuccess : BackupExportUiEvent
}