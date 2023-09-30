package com.twofasapp.feature.security.ui.lock

import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.PinScreenState

internal data class LockUiState(
    val digits: PinDigits = PinDigits.Code4,
    val lockMethod: LockMethod = LockMethod.Pin,
    val invalidPinStatus: InvalidPinStatus = InvalidPinStatus.Default,
    val errorMessage: Int? = null,
    val pinScreenState: PinScreenState = PinScreenState.Loading,
    val events: List<LockUiEvent> = emptyList(),
)

internal sealed interface LockUiEvent {
    data object ClearCurrentPin : LockUiEvent
    data object NotifyInvalidPin : LockUiEvent
    data object Finish : LockUiEvent
}