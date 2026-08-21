package com.twofasapp.feature.security.ui.setuppin

import androidx.lifecycle.ViewModel
import com.twofasapp.base.AuthTracker
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.locale.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

internal class SetupPinViewModel(
    private val securityRepository: SecurityRepository,
    private val authTracker: AuthTracker,
) : ViewModel() {

    val uiState = MutableStateFlow(SetupPinUiState())
    private var digitsOverridden = false

    init {
        launchScoped {
            securityRepository.observePinOptions().collect { options ->
                if (!digitsOverridden) {
                    uiState.update { it.copy(digits = options.digits) }
                }
            }
        }
    }

    fun onKeyClick(digit: Int) {
        val state = uiState.value
        if (state.verifying || state.enteredPin.length >= state.digits.value) return

        val pin = state.enteredPin + digit
        uiState.update { it.copy(enteredPin = pin, errorMessage = null) }

        if (pin.length == state.digits.value) {
            onPinComplete(pin)
        }
    }

    fun onBackspaceClick() {
        uiState.update { it.copy(enteredPin = it.enteredPin.dropLast(1)) }
    }

    fun onPinDigitsChanged(digits: PinDigits) {
        digitsOverridden = true
        uiState.update {
            it.copy(
                digits = digits,
                enteredPin = "",
                firstPin = "",
                message = R.string.security__enter_your_new_pin,
                errorMessage = null,
            )
        }
    }

    private fun onPinComplete(pin: String) {
        if (uiState.value.firstPin.isEmpty()) {
            launchScoped {
                delay(200.milliseconds)
                uiState.update {
                    it.copy(
                        firstPin = pin,
                        enteredPin = "",
                        showPinOptions = false,
                        message = R.string.security__confirm_new_pin,
                    )
                }
            }
        } else {
            confirm(pin)
        }
    }

    private fun confirm(pin: String) {
        val state = uiState.value

        if (pin == state.firstPin) {
            launchScoped {
                uiState.update { it.copy(verifying = true) }

                securityRepository.editPin(state.firstPin)
                securityRepository.editPinOptions(
                    securityRepository.observePinOptions().first().copy(digits = state.digits),
                )
                authTracker.onChangingLockStatus()

                uiState.update { it.copy(finished = true) }
            }
        } else {
            launchScoped {
                delay(200.milliseconds)
                uiState.update {
                    it.copy(
                        enteredPin = "",
                        errorMessage = R.string.security_error_no_match,
                        invalidPinCount = it.invalidPinCount + 1,
                    )
                }
            }
        }
    }
}