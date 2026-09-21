package com.twofasapp.feature.security.ui.changepin

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.locale.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

internal class ChangePinViewModel(
    private val securityRepository: SecurityRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(ChangePinUiState())

    init {
        launchScoped {
            securityRepository.observePinOptions().collect { options ->
                uiState.update { it.copy(digits = options.digits) }
            }
        }
    }

    fun onKeyClick(digit: Int) {
        val state = uiState.value
        if (state.verifying || state.enteredPin.length >= state.digits.value) return

        val pin = state.enteredPin + digit
        uiState.update { it.copy(enteredPin = pin, errorMessage = null) }

        if (pin.length == state.digits.value) {
            verify(pin)
        }
    }

    fun onBackspaceClick() {
        uiState.update { it.copy(enteredPin = it.enteredPin.dropLast(1)) }
    }

    private fun verify(pin: String) {
        launchScoped {
            uiState.update { it.copy(verifying = true) }

            if (pin == securityRepository.getPin()) {
                uiState.update { it.copy(currentPinVerified = true) }
            } else {
                delay(200.milliseconds)
                uiState.update {
                    it.copy(
                        verifying = false,
                        enteredPin = "",
                        errorMessage = R.string.security__pin_error_incorrect,
                        invalidPinCount = it.invalidPinCount + 1,
                    )
                }
            }
        }
    }
}