package com.twofasapp.security.ui.lock

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.resources.R
import com.twofasapp.security.domain.EditInvalidPinStatusCase
import com.twofasapp.security.domain.EditLockMethodCase
import com.twofasapp.security.domain.GetPinCase
import com.twofasapp.security.domain.ObserveInvalidPinStatusCase
import com.twofasapp.security.domain.ObserveLockMethodCase
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.ui.pin.PinScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LockViewModel(
    private val dispatchers: Dispatchers,
    private val observeLockMethodCase: ObserveLockMethodCase,
    private val observePinOptionsCase: ObservePinOptionsCase,
    private val observeInvalidPinStatusCase: ObserveInvalidPinStatusCase,
    private val editInvalidPinStatusCase: EditInvalidPinStatusCase,
    private val getPinCase: GetPinCase,
    private val editLockMethodCase: EditLockMethodCase,
) : BaseViewModel() {

    val uiState = MutableStateFlow(LockUiState())

    init {
        viewModelScope.launch(dispatchers.io) {
            combine(
                observeLockMethodCase.invoke(),
                observePinOptionsCase.invoke(),
                observeInvalidPinStatusCase.invoke(),
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
        viewModelScope.launch(dispatchers.io) {
            uiState.update { it.copy(pinScreenState = PinScreenState.Verifying) }

            if (pin == getPinCase()) {
                editInvalidPinStatusCase.reset()
                uiState.update { it.postEvent(LockUiState.Event.Finish) }
            } else {
                delay(150)

                editInvalidPinStatusCase.incrementAttempt()

                uiState.update {
                    it.copy(
                        pinScreenState = PinScreenState.Default,
                        errorMessage = R.string.security__pin_error_incorrect,
                    )
                        .postEvent(LockUiState.Event.NotifyInvalidPin)
                        .postEvent(LockUiState.Event.ClearCurrentPin)
                }
            }
        }

    }

    fun biometricsVerified() {
        viewModelScope.launch(dispatchers.io) {
            editInvalidPinStatusCase.reset()
            uiState.update { it.postEvent(LockUiState.Event.Finish) }
        }
    }

    fun eventHandled(id: String) {
        uiState.update { it.reduceEvent(id) }
    }

    fun refreshBlockTimeLeft() {
        viewModelScope.launch(dispatchers.io) {
            uiState.update { it.copy(invalidPinStatus = observeInvalidPinStatusCase().first()) }
        }
    }

    fun disableBiometric() {
        launchScoped {
            editLockMethodCase.invoke(LockMethod.Pin)
            uiState.emit(uiState.value.copy(errorMessage = R.string.fingerprint__biometric_invalidated))
        }
    }
}