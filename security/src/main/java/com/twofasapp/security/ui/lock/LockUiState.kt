package com.twofasapp.security.ui.lock

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.security.domain.model.InvalidPinStatus
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.ui.pin.PinScreenState

internal data class LockUiState(
    val digits: PinDigits = PinDigits.Code4,
    val lockMethod: LockMethod = LockMethod.Pin,
    val invalidPinStatus: InvalidPinStatus = InvalidPinStatus.Default,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Loading,
    override val events: List<Event> = emptyList(),
) : UiState<LockUiState, LockUiState.Event> {

    sealed class Event : UiEvent() {
        object ClearCurrentPin : Event()
        object NotifyInvalidPin : Event()
        object Finish : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): LockUiState {
        return copy(events = events)
    }
}