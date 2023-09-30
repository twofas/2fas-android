package com.twofasapp.feature.security.ui.changepin

import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.PinScreenState

internal data class ChangePinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Default,
    val events: List<ChangePinUiEvent> = emptyList(),
)

internal sealed interface ChangePinUiEvent {
    data object ClearCurrentPin : ChangePinUiEvent
    data object NotifyInvalidPin : ChangePinUiEvent
    data object NavigateToSetup : ChangePinUiEvent
}
