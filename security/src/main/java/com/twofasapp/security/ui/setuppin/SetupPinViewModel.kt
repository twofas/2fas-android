package com.twofasapp.security.ui.setuppin

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.BaseViewModel
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.resources.R
import com.twofasapp.security.domain.EditPinCase
import com.twofasapp.security.domain.EditPinOptionsCase
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.ui.pin.PinScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupPinViewModel(
    private val dispatchers: Dispatchers,
    private val editPinOptionsCase: EditPinOptionsCase,
    private val observePinOptionsCase: ObservePinOptionsCase,
    private val editPinCase: EditPinCase,
    private val authTracker: AuthTracker,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SetupPinUiState())
    val uiState = _uiState.asStateFlow()

    private val tmpPinDigitsFlow: MutableStateFlow<PinDigits?> = MutableStateFlow(null)

    private var enteredPin = ""

    init {
        viewModelScope.launch(dispatchers.io) {
            combine(
                observePinOptionsCase(),
                tmpPinDigitsFlow,
            ) { a, b -> Pair(a, b) }
                .collect { (pinOptions, tmpPinDigits) ->
                    _uiState.update { state -> state.copy(digits = tmpPinDigits ?: pinOptions.digits) }
                }
        }
    }

    fun pinEntered(pin: String) {
        if (enteredPin.isBlank()) {
            enteredPin = pin

            viewModelScope.launch(dispatchers.io) {
                delay(200)
                _uiState.update { state ->
                    state.copy(
                        showPinOptions = false,
                        message = R.string.security__confirm_new_pin
                    )
                        .postEvent(SetupPinUiState.Event.ClearCurrentPin)
                }
            }
        } else {
            confirmPinEntered(pin)
        }
    }

    private fun confirmPinEntered(pin: String) {
        if (pin == enteredPin) {
            viewModelScope.launch(dispatchers.io) {
                _uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

                editPinCase.invoke(enteredPin)

                editPinOptionsCase.invoke(
                    observePinOptionsCase.invoke().first().copy(
                        digits = uiState.value.digits
                    )
                )

                authTracker.onChangingLockStatus()

                _uiState.update {
                    it.postEvent(SetupPinUiState.Event.Finish)
                }
            }

        } else {
            viewModelScope.launch(dispatchers.io) {
                delay(200)
                _uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security_error_no_match,
                    )
                        .postEvent(SetupPinUiState.Event.NotifyInvalidPin)
                        .postEvent(SetupPinUiState.Event.ClearCurrentPin)
                }
            }
        }
    }

    fun eventHandled(id: String) {
        _uiState.update { it.reduceEvent(id) }
    }

    fun pinDigitsChanged(pinDigits: PinDigits) {
        tmpPinDigitsFlow.tryEmit(pinDigits)
    }
}