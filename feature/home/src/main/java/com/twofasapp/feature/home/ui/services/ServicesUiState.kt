package com.twofasapp.feature.home.ui.services

import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.domain.AppSettings

data class ServicesUiState(
    val groups: List<Group> = emptyList(),
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = true,
    val isInEditMode: Boolean = false,
    val appSettings: AppSettings = AppSettings(),
    val events: List<ServicesStateEvent> = listOf(),
) {
    fun getService(id: Long): Service? {
        return services.firstOrNull() { it.id == id }
    }
}

sealed interface ServicesStateEvent {
    object ShowAddServiceModal : ServicesStateEvent
}