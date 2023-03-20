package com.twofasapp.feature.home.ui.services

import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.domain.AppSettings

data class ServicesUiState(
    val groups: List<Group> = emptyList(),
    val services: List<Service> = emptyList(),
    val totalGroups: Int = 0,
    val totalServices: Int = 0,
    val isLoading: Boolean = true,
    val isInEditMode: Boolean = false,
    val searchQuery: String = "",
    val searchFocused: Boolean = false,
    val showSyncNoticeBar: Boolean = false,
    val showSyncReminder: Boolean = true,
    val appSettings: AppSettings = AppSettings(),
    val events: List<ServicesStateEvent> = listOf(),
    val items: List<ServicesListItem> = emptyList()
) {
    fun getService(id: Long): Service? {
        return services.firstOrNull() { it.id == id }
    }
}

sealed interface ServicesStateEvent {
    object ShowAddServiceModal : ServicesStateEvent
    data class ShowServiceAddedModal(val id: Long) : ServicesStateEvent
}