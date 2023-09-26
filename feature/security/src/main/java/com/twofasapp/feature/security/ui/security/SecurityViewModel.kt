package com.twofasapp.feature.security.ui.security

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinOptions
import com.twofasapp.data.session.domain.PinTimeout
import com.twofasapp.data.session.domain.PinTrials
import com.twofasapp.data.session.work.DisableScreenshotsWorkDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class SecurityViewModel(
    private val securityRepository: SecurityRepository,
    private val settingsRepository: SettingsRepository,
    private val disableScreenshotsWorkDispatcher: DisableScreenshotsWorkDispatcher,
) : ViewModel() {

    val uiState = MutableStateFlow(SecurityUiState())

    init {
        launchScoped {
            combine(
                securityRepository.observeLockMethod(),
                securityRepository.observePinOptions(),
            ) { a, b -> Pair(a, b) }
                .collect { (lockMethod, pinOptions) ->
                    uiState.update { state ->
                        state.copy(
                            lockMethod = lockMethod,
                            pinTrials = pinOptions.trials,
                            pinTimeout = pinOptions.timeout,
                            pinDigits = pinOptions.digits
                        )
                    }
                }
        }

        launchScoped {
            settingsRepository.observeAppSettings().collect { appSettings ->
                uiState.update { it.copy(allowScreenshots = appSettings.allowScreenshots) }
            }
        }
    }

    fun updatePinTrails(pinTrials: PinTrials) {
        updatePinOptions { it.copy(trials = pinTrials) }
    }

    fun updatePinTimeout(pinTimeout: PinTimeout) {
        updatePinOptions { it.copy(timeout = pinTimeout) }
    }

    fun updateBiometricLock(isEnabled: Boolean) {
        launchScoped {
            val method = if (isEnabled) LockMethod.Biometrics else LockMethod.Pin
            securityRepository.editLockMethod(method)
        }
    }

    fun toggleScreenshots() {
        val isAllowed = uiState.value.allowScreenshots.not()

        launchScoped {
            settingsRepository.setAllowScreenshots(isAllowed)
        }

        if (isAllowed) {
            disableScreenshotsWorkDispatcher.dispatch()
        }
    }

    private fun updatePinOptions(action: (PinOptions) -> PinOptions) {
        launchScoped {
            securityRepository.editPinOptions(
                action.invoke(
                    PinOptions(
                        digits = uiState.value.pinDigits,
                        trials = uiState.value.pinTrials,
                        timeout = uiState.value.pinTimeout,
                    )
                )
            )
        }
    }
}