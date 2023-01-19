package com.twofasapp.security.ui.changepin

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.ui.pin.PinScreenState

data class ChangePinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Default,
    override val events: List<Event> = emptyList(),
) : UiState<ChangePinUiState, ChangePinUiState.Event> {

    sealed class Event : UiEvent() {
        object ClearCurrentPin : Event()
        object NotifyInvalidPin : Event()
        object NavigateToSetup : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): ChangePinUiState {
        return copy(events = events)
    }
}