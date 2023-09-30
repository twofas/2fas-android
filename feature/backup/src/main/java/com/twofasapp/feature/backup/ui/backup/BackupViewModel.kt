package com.twofasapp.feature.backup.ui.backup

import androidx.activity.result.ActivityResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.cloud.googleauth.GoogleAuth
import com.twofasapp.data.cloud.googleauth.SignInResult
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.CloudSyncError
import com.twofasapp.data.services.domain.CloudSyncStatus
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update

internal class BackupViewModel(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val servicesRepository: ServicesRepository,
    private val backupRepository: BackupRepository,
    private val googleAuth: GoogleAuth,
) : ViewModel() {

    private val autoTurnOnBackup: Boolean = savedStateHandle.getOrThrow(NavArg.TurnOnBackup.name)

    val uiState: MutableStateFlow<BackupUiState> = MutableStateFlow(BackupUiState())

    init {
        launchScoped {
            servicesRepository.observeServices().distinctUntilChangedBy { it.size }.collect { services ->
                uiState.update { it.copy(exportEnabled = services.isNotEmpty()) }
            }
        }

        launchScoped {
            combine(
                backupRepository.observeCloudBackupStatus(),
                backupRepository.observeCloudSyncStatus(),
            ) { a, b -> Pair(a, b) }.collect { (cloudBackupStatus, cloudSyncStatus) ->

                uiState.update {
                    it.copy(
                        showSyncMsg = cloudBackupStatus.active.not(),
                        cloudBackupStatus = cloudBackupStatus,
                        cloudSyncStatus = cloudSyncStatus,
                    )
                }

                when (cloudSyncStatus) {
                    is CloudSyncStatus.Default,
                    is CloudSyncStatus.Synced -> {
                        uiState.update {
                            it.copy(
                                syncChecked = cloudBackupStatus.active,
                                syncEnabled = true,
                                showError = false,
                                error = null,
                            )
                        }

                        if (cloudBackupStatus.active && autoTurnOnBackup && cloudSyncStatus is CloudSyncStatus.Synced) {
                            publishEvent(BackupUiEvent.FinishSuccess)
                        }
                    }

                    is CloudSyncStatus.Syncing -> {
                        uiState.update {
                            it.copy(
                                syncChecked = cloudBackupStatus.active,
                                syncEnabled = false,
                                showError = false,
                                error = null,
                            )
                        }
                    }

                    is CloudSyncStatus.Error -> {
                        val isPasswordError = cloudSyncStatus.error == CloudSyncError.DecryptWrongPassword ||
                                cloudSyncStatus.error == CloudSyncError.DecryptNoPassword

                        uiState.update {
                            it.copy(
                                syncChecked = cloudBackupStatus.active && isPasswordError.not(),
                                syncEnabled = true,
                                showError = true,
                                error = cloudSyncStatus.error,
                            )
                        }

                        if (isPasswordError && cloudSyncStatus.trigger == CloudSyncTrigger.FirstConnect) {
                            publishEvent(BackupUiEvent.ShowPasswordDialog)
                        }
                    }
                }
            }
        }

        if (autoTurnOnBackup) {
            turnOnSync()
        }
    }

    fun turnOnSync() {
        launchScoped {
            publishEvent(BackupUiEvent.SignInToGoogle(signInIntent = googleAuth.createSignInIntent()))
        }
    }

    fun turnOffSync() {
        launchScoped {
            googleAuth.signOut()
            backupRepository.setCloudSyncNotConfigured()
            sessionRepository.resetBackupReminder()
        }
    }

    fun enterPassword(password: String) {
        launchScoped {
            val isCorrect = backupRepository.checkCloudBackupPassword(password)

            if (isCorrect) {
                backupRepository.dispatchCloudSync(
                    trigger = (uiState.value.cloudSyncStatus as? CloudSyncStatus.Error)?.trigger ?: CloudSyncTrigger.FirstConnect,
                    password = password,
                )
            } else {
                publishEvent(BackupUiEvent.ShowPasswordDialogError)
            }
        }
    }

    fun handleSignInResult(result: ActivityResult) {
        launchScoped {
            when (val signInResult = googleAuth.handleSignInResult(result)) {
                is SignInResult.Success -> {
                    backupRepository.setCloudSyncActive(signInResult.email)
                    backupRepository.dispatchCloudSync(CloudSyncTrigger.FirstConnect)
                }

                is SignInResult.Canceled -> {
                    when (signInResult.reason) {
                        SignInResult.CancelReason.PermissionNotGranted -> publishEvent(BackupUiEvent.SignInPermissionError)
                        SignInResult.CancelReason.NetworkError -> publishEvent(BackupUiEvent.SignInNetworkError)
                        SignInResult.CancelReason.Canceled -> Unit
                    }
                }

                is SignInResult.Failure -> publishEvent(BackupUiEvent.SignInUnknownError(signInResult.reason.message))
            }
        }
    }

    fun consumeEvent(event: BackupUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: BackupUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event).distinct()) }
    }
}