package com.twofasapp.feature.backup.ui.import

import android.net.Uri
import com.twofasapp.data.services.domain.BackupContent

internal data class BackupImportUiState(
    val fileUri: Uri? = null,
    val backupContent: BackupContent? = null,
    val screenState: ScreenState? = null,
    val importing: Boolean = false,
    val events: List<BackupImportUiEvent> = emptyList(),
)

internal sealed interface ScreenState {
    data class BackupRead(val servicesToImport: Int) : ScreenState
    data object BackupReadEncrypted : ScreenState
    data object ErrorInvalidFile : ScreenState
    data object ErrorInvalidFileSize : ScreenState
}

internal sealed interface BackupImportUiEvent {
    data object ShowFilePicker : BackupImportUiEvent
    data object WrongPassword : BackupImportUiEvent
    data object DecryptError : BackupImportUiEvent
    data object ImportSuccess : BackupImportUiEvent
    data class ImportError(val msg: String?) : BackupImportUiEvent
}