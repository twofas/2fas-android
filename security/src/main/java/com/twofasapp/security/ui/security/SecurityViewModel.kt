package com.twofasapp.security.ui.security

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.security.domain.EditLockMethodCase
import com.twofasapp.security.domain.EditPinOptionsCase
import com.twofasapp.security.domain.ObserveLockMethodCase
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinOptions
import com.twofasapp.security.domain.model.PinTimeout
import com.twofasapp.security.domain.model.PinTrials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SecurityViewModel(
    private val dispatchers: Dispatchers,
    private val observeLockMethodCase: ObserveLockMethodCase,
    private val observePinOptionsCase: ObservePinOptionsCase,
    private val editPinOptionsCase: EditPinOptionsCase,
    private val editLockMethodCase: EditLockMethodCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io()) {
            combine(
                observeLockMethodCase.invoke(),
                observePinOptionsCase.invoke()
            ) { a, b -> Pair(a, b) }
                .collect { (lockMethod, pinOptions) ->
                    _uiState.update { state ->
                        state.copy(
                            lockMethod = lockMethod,
                            pinTrials = pinOptions.trials,
                            pinTimeout = pinOptions.timeout,
                            pinDigits = pinOptions.digits
                        )
                    }
                }
        }
    }

    fun updatePinTrails(pinTrials: PinTrials) {
        updatePinOptions { it.copy(trials = pinTrials) }
    }

    fun updatePinTimeout(pinTimeout: PinTimeout) {
        updatePinOptions { it.copy(timeout = pinTimeout) }
    }

    fun updateBiometricLock(isEnabled: Boolean) {
        viewModelScope.launch(dispatchers.io()) {
            val method = if (isEnabled) LockMethod.Biometrics else LockMethod.Pin
            editLockMethodCase(method)
        }
    }

    private fun updatePinOptions(action: (PinOptions) -> PinOptions) {
        viewModelScope.launch(dispatchers.io()) {
            editPinOptionsCase(
                action.invoke(
                    PinOptions(
                        digits = uiState.value.pinDigits,
                        trials = uiState.value.pinTrials,
                        timeout = uiState.value.pinTimeout,
                    )
                )
            )
        }
    }
}