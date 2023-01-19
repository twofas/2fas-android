package com.twofasapp.notifications.ui

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.notifications.domain.model.Notification

internal data class NotificationsUiState(
    val items: List<Notification> = emptyList(),
    override val events: List<Event> = emptyList()
) : UiState<NotificationsUiState, NotificationsUiState.Event> {

    sealed class Event : UiEvent() {
        class OpenBrowser(val url: String): Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): NotificationsUiState {
        return copy(events = events)
    }
}