package com.twofasapp.security.ui.changepin

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.locale.R
import com.twofasapp.security.domain.GetPinCase
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.ui.pin.PinScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePinViewModel(
    private val dispatchers: Dispatchers,
    private val observePinOptionsCase: ObservePinOptionsCase,
    private val getPinCase: GetPinCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChangePinUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io) {
            observePinOptionsCase().collect {
                _uiState.update { state -> state.copy(digits = it.digits) }
            }
        }
    }

    fun pinEntered(pin: String) {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

            if (pin == getPinCase()) {
                _uiState.update { it.postEvent(ChangePinUiState.Event.NavigateToSetup) }
            } else {
                delay(200)

                _uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security__pin_error_incorrect,
                    )
                        .postEvent(ChangePinUiState.Event.NotifyInvalidPin)
                        .postEvent(ChangePinUiState.Event.ClearCurrentPin)
                }
            }
        }

    }

    fun eventHandled(id: String) {
        _uiState.update { it.reduceEvent(id) }
    }
}