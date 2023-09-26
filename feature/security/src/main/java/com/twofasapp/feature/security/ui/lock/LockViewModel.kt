package com.twofasapp.feature.security.ui.lock

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.feature.security.ui.pin.PinScreenState
import com.twofasapp.locale.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class LockViewModel(
    private val securityRepository: SecurityRepository,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    val uiState = MutableStateFlow(LockUiState())

    init {
        launchScoped {
            combine(
                securityRepository.observeLockMethod(),
                securityRepository.observePinOptions(),
                securityRepository.observeInvalidPinStatus(),
            ) { a, b, c -> Triple(a, b, c) }
                .collect { (lockMethod, pinOptions, invalidPinStatus) ->
                    uiState.update { state ->
                        state.copy(
                            lockMethod = lockMethod,
                            invalidPinStatus = invalidPinStatus,
                            digits = pinOptions.digits,
                            pinScreenState = PinScreenState.Default,
                        )
                    }
                }
        }
    }

    fun pinEntered(pin: String) {
        launchScoped {
            uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

            if (pin == securityRepository.getPin()) {
                securityRepository.editInvalidPinStatus(InvalidPinStatus.Default)
                publishEvent(LockUiEvent.Finish)
            } else {
                delay(150)

                // Increment attempt
                with(securityRepository.observeInvalidPinStatus().first()) {
                    securityRepository.editInvalidPinStatus(
                        copy(
                            attempts = attempts + 1,
                            lastAttemptSinceBootMs = timeProvider.systemElapsedTime()
                        )
                    )
                }

                uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security__pin_error_incorrect,
                    )
                }
                publishEvent(LockUiEvent.NotifyInvalidPin)
                publishEvent(LockUiEvent.ClearCurrentPin)
            }
        }

    }

    fun biometricsVerified() {
        launchScoped {
            securityRepository.editInvalidPinStatus(InvalidPinStatus.Default)
            publishEvent(LockUiEvent.Finish)
        }
    }

    fun refreshBlockTimeLeft() {
        launchScoped {
            uiState.update { it.copy(invalidPinStatus = securityRepository.observeInvalidPinStatus().first()) }
        }
    }

    fun disableBiometric() {
        launchScoped {
            securityRepository.editLockMethod(LockMethod.Pin)
            uiState.emit(uiState.value.copy(errorMessage = R.string.fingerprint__biometric_invalidated))
        }
    }

    fun consumeEvent(event: LockUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: LockUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }
}