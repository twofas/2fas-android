package com.twofasapp.feature.home.ui.services

import com.twofasapp.data.services.domain.Service

data class ServicesUiState(
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = true,
    val isNextTokenEnabled: Boolean = false,
    val isInEditMode: Boolean = false,
    val events: List<ServicesStateEvent> = listOf(),
) {
    fun getService(id: Long): Service {
        return services.first { it.id == id }
    }
}

sealed interface ServicesStateEvent {
    object ShowAddServiceModal : ServicesStateEvent
}