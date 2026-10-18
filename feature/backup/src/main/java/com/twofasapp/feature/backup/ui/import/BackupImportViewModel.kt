package com.twofasapp.feature.backup.ui.import

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.common.logger.Flog
import com.twofasapp.core.design.foundation.dialog.formatErrorDetails
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.exceptions.DecryptWrongPassword
import com.twofasapp.data.services.exceptions.FileTooBigException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BackupImportViewModel(
    importFileUri: String?,
    private val backupRepository: BackupRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupImportUiState> = MutableStateFlow(BackupImportUiState())

    init {
        if (importFileUri == null) {
            publishEvent(BackupImportUiEvent.ShowFilePicker)
        } else {
            fileOpened(importFileUri.toUri())
        }
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
                    Flog.persist(
                        tag = "Backup",
                        message = "Import read: success encrypted=${backupContent.isEncrypted} schema=${backupContent.schemaVersion}",
                    )

                    if (backupContent.isEncrypted) {
                        uiState.update {
                            it.copy(
                                screenState = ScreenState.BackupReadEncrypted,
                                backupContent = backupContent,
                            )
                        }
                    } else {
                        uiState.update {
                            it.copy(
                                backupContent = backupContent,
                                screenState = ScreenState.BackupRead(backupContent.services.size),
                            )
                        }
                    }
                }
                .onFailure { e ->
                    // Exception details are not persisted as they may contain backup content
                    Flog.persist(tag = "Backup", message = "Import read: failed (${e::class.simpleName})")

                    when (e) {
                        is FileTooBigException -> uiState.update { it.copy(screenState = ScreenState.ErrorInvalidFileSize) }
                        else -> uiState.update { it.copy(screenState = ScreenState.ErrorInvalidFile(reason = e.formatErrorDetails())) }
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
                        password = password.ifEmpty { null },
                    )
                }
                    .onSuccess { decrypted -> startImport(decrypted) }
                    .onFailure { exception ->
                        Flog.persist(tag = "Backup", message = "Import decrypt: failed (${exception::class.simpleName})")

                        when (exception) {
                            is DecryptWrongPassword -> publishEvent(BackupImportUiEvent.WrongPassword)
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
                    e.printStackTrace()
                    Flog.persist(tag = "Backup", message = "Import: failed (${e::class.simpleName}) schema=${backupContent.schemaVersion}")
                    uiState.update { it.copy(importing = false) }

                    if (BackupContent.CurrentSchema < backupContent.schemaVersion) {
                        publishEvent(
                            BackupImportUiEvent.InvalidSchemaError(
                                currentVersion = BackupContent.CurrentSchema,
                                importingVersion = backupContent.schemaVersion,
                            ),
                        )
                    } else {
                        publishEvent(BackupImportUiEvent.ImportError(e.stackTraceToString()))
                    }
                }
        }
    }
}