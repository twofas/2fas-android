package com.twofasapp.feature.backup.ui.export

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.FileOutputStream

internal class BackupExportViewModel(
    private val context: Application,
    private val appBuild: AppBuild,
    private val backupRepository: BackupRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupExportUiState> = MutableStateFlow(BackupExportUiState())

    fun togglePassword() {
        uiState.update { it.copy(passwordChecked = it.passwordChecked.not()) }
    }

    fun updatePassword(password: String) {
        uiState.update { it.copy(password = password) }
    }

    fun shareBackup() {
        launchScoped {
            runSafely {
                backupRepository.createBackupContentSerialized(
                    password = if (uiState.value.passwordChecked) uiState.value.password else null,
                )
            }
                .onSuccess { content ->
                    publishEvent(
                        BackupExportUiEvent.ShowSharePicker(
                            appId = appBuild.id,
                            content = content,
                        ),
                    )
                }
                .onFailure { publishEvent(BackupExportUiEvent.ShareError) }
        }
    }

    fun downloadBackup(fileUri: Uri) {
        launchScoped {
            runSafely {
                val content = backupRepository.createBackupContentSerialized(
                    password = if (uiState.value.passwordChecked) uiState.value.password else null,
                )

                val bytes = content.toByteArray(Charsets.UTF_8)

                // Open with explicit truncation ("wt"). Some document providers (e.g. cloud storage)
                // do not truncate on plain "w", leaving a tail of the previous, longer file.
                // Fall back to "w" for providers that reject "wt" and truncate manually when possible.
                val outputStream = runCatching { context.contentResolver.openOutputStream(fileUri, "wt") }.getOrNull()
                    ?: context.contentResolver.openOutputStream(fileUri, "w")

                outputStream?.use { stream ->
                    stream.write(bytes)
                    (stream as? FileOutputStream)?.channel?.truncate(bytes.size.toLong())
                }
            }
                .onSuccess {
                    publishEvent(BackupExportUiEvent.DownloadSuccess)
                }
                .onFailure { publishEvent(BackupExportUiEvent.DownloadError) }
        }
    }

    fun consumeEvent(event: BackupExportUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: BackupExportUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }
}