package com.twofasapp.security.ui.disablepin

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.resources.R
import com.twofasapp.security.domain.EditPinCase
import com.twofasapp.security.domain.GetPinCase
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.ui.pin.PinScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisablePinViewModel(
    private val dispatchers: Dispatchers,
    private val observePinOptionsCase: ObservePinOptionsCase,
    private val editPinCase: EditPinCase,
    private val getPinCase: GetPinCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DisablePinUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io()) {
            observePinOptionsCase().collect {
                _uiState.update { state -> state.copy(digits = it.digits) }
            }
        }
    }

    fun pinEntered(pin: String) {
        viewModelScope.launch(dispatchers.io()) {
            _uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

            if (pin == getPinCase()) {
                editPinCase.invoke("")
                _uiState.update { it.postEvent(DisablePinUiState.Event.Finish) }
            } else {
                delay(200)

                _uiState.update {
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
        _uiState.update { it.reduceEvent(id) }
    }
}