package com.twofasapp.security.ui.lock

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinDigits
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
        data object ClearCurrentPin : Event()
        data object NotifyInvalidPin : Event()
        data object Finish : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): LockUiState {
        return copy(events = events)
    }
}