package com.twofasapp.feature.security.ui.setuppin

import androidx.lifecycle.ViewModel
import com.twofasapp.base.AuthTracker
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.PinScreenState
import com.twofasapp.locale.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class SetupPinViewModel(
    private val securityRepository: SecurityRepository,
    private val authTracker: AuthTracker,
) : ViewModel() {

    val uiState = MutableStateFlow(SetupPinUiState())

    private val tmpPinDigitsFlow: MutableStateFlow<PinDigits?> = MutableStateFlow(null)

    private var enteredPin = ""

    init {
        launchScoped {
            combine(
                securityRepository.observePinOptions(),
                tmpPinDigitsFlow,
            ) { a, b -> Pair(a, b) }
                .collect { (pinOptions, tmpPinDigits) ->
                    uiState.update { state -> state.copy(digits = tmpPinDigits ?: pinOptions.digits) }
                }
        }
    }

    fun pinEntered(pin: String) {
        if (enteredPin.isBlank()) {
            enteredPin = pin

            launchScoped {
                delay(200)
                uiState.update { state ->
                    state.copy(
                        showPinOptions = false,
                        message = R.string.security__confirm_new_pin
                    )
                }

                publishEvent(SetupPinUiEvent.ClearCurrentPin)
            }
        } else {
            confirmPinEntered(pin)
        }
    }

    private fun confirmPinEntered(pin: String) {
        if (pin == enteredPin) {
            launchScoped {
                uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

                securityRepository.editPin(enteredPin)

                securityRepository.editPinOptions(
                    securityRepository.observePinOptions().first().copy(
                        digits = uiState.value.digits
                    )
                )

                authTracker.onChangingLockStatus()

                publishEvent(SetupPinUiEvent.Finish)
            }

        } else {
            launchScoped {
                delay(200)
                uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security_error_no_match,
                    )
                }

                publishEvent(SetupPinUiEvent.NotifyInvalidPin)
                publishEvent(SetupPinUiEvent.ClearCurrentPin)

            }
        }
    }

    fun pinDigitsChanged(pinDigits: PinDigits) {
        tmpPinDigitsFlow.tryEmit(pinDigits)
    }

    fun consumeEvent(event: SetupPinUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: SetupPinUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }
}