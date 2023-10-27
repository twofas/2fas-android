package com.twofasapp.feature.security.ui.disablepin

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.feature.security.ui.pin.PinScreenState
import com.twofasapp.locale.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DisablePinViewModel(
    private val securityRepository: SecurityRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(DisablePinUiState())

    init {
        launchScoped {
            securityRepository.observePinOptions().collect {
                uiState.update { state -> state.copy(digits = it.digits) }
            }
        }
    }

    fun pinEntered(pin: String) {
        launchScoped {
            uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

            if (pin == securityRepository.getPin()) {
                securityRepository.editPin("")
                publishEvent(DisablePinUiEvent.Finish)
            } else {
                delay(200)

                uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security__pin_error_incorrect,
                    )
                }

                publishEvent(DisablePinUiEvent.NotifyInvalidPin)
                publishEvent(DisablePinUiEvent.ClearCurrentPin)
            }
        }

    }

    fun consumeEvent(event: DisablePinUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }

    private fun publishEvent(event: DisablePinUiEvent) {
        uiState.update { it.copy(events = it.events.plus(event)) }
    }
}