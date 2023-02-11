package com.twofasapp.browserextension.ui.browser

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import java.time.Instant

data class BrowserDetailsUiState(
    val extensionId: String = "",
    val browserName: String = "",
    val browserPairedAt: Instant? = null,
    val showConfirmDeleteDialog: Boolean = false,
    override val events: List<Event> = emptyList(),
) : UiState<BrowserDetailsUiState, BrowserDetailsUiState.Event> {

    sealed class Event : UiEvent() {
        class ShowSnackbarError(val message: String) : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): BrowserDetailsUiState {
        return copy(events = events)
    }
}