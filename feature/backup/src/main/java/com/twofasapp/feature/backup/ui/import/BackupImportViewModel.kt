package com.twofasapp.feature.backup.ui.import

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.exceptions.FileTooBigException
import com.twofasapp.data.services.exceptions.ImportNoPassword
import com.twofasapp.data.services.exceptions.ImportWrongPassword
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BackupImportViewModel(
    savedStateHandle: SavedStateHandle,
    private val backupRepository: BackupRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupImportUiState> = MutableStateFlow(BackupImportUiState())

    init {
        // TODO: Refactor - handle incoming intent

        publishEvent(BackupImportUiEvent.ShowFilePicker)
    }

    fun consumeEvent(event: BackupImportUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: BackupImportUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }

    fun fileOpened(fileUri: Uri) {
        uiState.update { it.copy(fileUri = fileUri) }

        launchScoped {
            runSafely { backupRepository.readBackupContent(fileUri) }
                .onSuccess { backupContent ->
                    uiState.update {
                        it.copy(
                            backupContent = backupContent,
                            screenState = ScreenState.BackupRead(backupContent.services.size)
                        )
                    }
                }
                .onFailure { exception ->
                    when (exception) {
                        is FileTooBigException -> uiState.update { it.copy(screenState = ScreenState.ErrorInvalidFileSize) }
                        is ImportNoPassword -> uiState.update {
                            it.copy(
                                screenState = ScreenState.BackupReadEncrypted,
                                backupContent = exception.backupContent
                            )
                        }

                        else -> uiState.update { it.copy(screenState = ScreenState.ErrorInvalidFile) }
                    }
                }
        }
    }

    fun import(password: String? = null) {
        with(uiState.value) {
            if (backupContent == null) {
                return
            }

            if (backupContent.isEncrypted.not()) {
                startImport(backupContent)
                return
            }

            if (password.isNullOrBlank()) {
                return
            }

            launchScoped {
                runSafely {
                    backupRepository.decryptBackupContent(
                        backupContent = backupContent,
                        password = password,
                    )
                }
                    .onSuccess { decrypted -> startImport(decrypted) }
                    .onFailure { exception ->
                        when (exception) {
                            is ImportWrongPassword -> publishEvent(BackupImportUiEvent.WrongPassword)
                            else -> publishEvent(BackupImportUiEvent.DecryptError)
                        }
                    }
            }
        }
    }

    private fun startImport(backupContent: BackupContent) {
        uiState.update { it.copy(importing = true) }

        launchScoped {
            runSafely { backupRepository.import(backupContent) }
                .onSuccess { publishEvent(BackupImportUiEvent.ImportSuccess) }
                .onFailure { e ->
                    uiState.update { it.copy(importing = false) }
                    publishEvent(BackupImportUiEvent.ImportError(e.message))
                }
        }
    }
}