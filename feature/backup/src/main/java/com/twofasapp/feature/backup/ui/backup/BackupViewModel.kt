package com.twofasapp.feature.backup.ui.backup

import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.cloud.googleauth.GoogleAuth
import com.twofasapp.data.cloud.googleauth.SignInResult
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update

internal class BackupViewModel(
    private val sessionRepository: SessionRepository,
    private val servicesRepository: ServicesRepository,
    private val backupRepository: BackupRepository,
    private val googleAuth: GoogleAuth,
) : ViewModel() {

    val uiState: MutableStateFlow<BackupUiState> = MutableStateFlow(BackupUiState())

    init {
        launchScoped {
            servicesRepository.observeServices().distinctUntilChangedBy { it.size }.collect { services ->
                uiState.update { it.copy(exportEnabled = services.isNotEmpty()) }
            }
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
            uiState.update { it.copy(syncChecked = false) }
        }
    }

    fun consumeEvent(event: BackupUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: BackupUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }

    fun handleSignInResult(result: ActivityResult) {
        launchScoped {
            when (val signInResult = googleAuth.handleSignInResult(result)) {
                is SignInResult.Success -> {
                    uiState.update { it.copy(syncChecked = true) }

                    backupRepository.setCloudSyncActive(signInResult.email)
                    backupRepository.dispatchSync(CloudSyncTrigger.FirstConnect)
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
}