package com.twofasapp.security.ui.disablepin

import com.twofasapp.base.BaseViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.locale.R
import com.twofasapp.security.ui.pin.PinScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DisablePinViewModel(
    private val securityRepository: SecurityRepository,
) : BaseViewModel() {

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
                uiState.update { it.postEvent(DisablePinUiState.Event.Finish) }
            } else {
                delay(200)

                uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security__pin_error_incorrect,
                    )
                        .postEvent(DisablePinUiState.Event.NotifyInvalidPin)
                        .postEvent(DisablePinUiState.Event.ClearCurrentPin)
                }
            }
        }

    }

    fun eventHandled(id: String) {
        uiState.update { it.reduceEvent(id) }
    }
}