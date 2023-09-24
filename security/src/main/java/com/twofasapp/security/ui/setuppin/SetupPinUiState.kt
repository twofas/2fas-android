package com.twofasapp.security.ui.setuppin

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.locale.R
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.ui.pin.PinScreenState

data class SetupPinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val showPinOptions: Boolean = true,
    val message: Int = R.string.security__enter_your_new_pin,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Default,
    override val events: List<Event> = emptyList(),
) : UiState<SetupPinUiState, SetupPinUiState.Event> {

    sealed class Event : UiEvent() {
        object ClearCurrentPin : Event()
        object NotifyInvalidPin : Event()
        object Finish : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): SetupPinUiState {
        return copy(events = events)
    }
}