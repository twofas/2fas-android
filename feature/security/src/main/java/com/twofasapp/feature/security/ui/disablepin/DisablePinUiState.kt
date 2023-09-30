package com.twofasapp.feature.security.ui.disablepin

import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.PinScreenState

internal data class DisablePinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Default,
    val events: List<DisablePinUiEvent> = emptyList(),
)

internal sealed interface DisablePinUiEvent {
    data object ClearCurrentPin : DisablePinUiEvent
    data object NotifyInvalidPin : DisablePinUiEvent
    data object Finish : DisablePinUiEvent
}