package com.twofasapp.feature.security.ui.setuppin

import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.PinScreenState
import com.twofasapp.locale.R

internal data class SetupPinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val showPinOptions: Boolean = true,
    val message: Int = R.string.security__enter_your_new_pin,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Default,
    val events: List<SetupPinUiEvent> = emptyList(),
)

internal sealed interface SetupPinUiEvent {
    data object ClearCurrentPin : SetupPinUiEvent
    data object NotifyInvalidPin : SetupPinUiEvent
    data object Finish : SetupPinUiEvent
}