package com.twofasapp.feature.home.ui.services

import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle

data class HomeUiState(
    val developerModeEnabled: Boolean = false,
    val services: List<Service> = emptyList(),
    val groups: List<Group> = emptyList(),
    val totalGroups: Int = 0,
    val totalServices: Int = 0,
    val isLoading: Boolean = true,
    val isInEditMode: Boolean = false,
    val selectedServiceIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val searchFocused: Boolean = false,
    val showSyncNoticeBar: Boolean = false,
    val showSyncReminder: Boolean = true,
    val showAppReview: Boolean = false,
    val showPassBanner: Boolean = false,
    val hasUnreadNotifications: Boolean = false,
    val appSettings: AppSettings = AppSettings(),
    val servicesSort: ServicesSort = ServicesSort.Manual,
    val servicesStyle: ServicesStyle = ServicesStyle.Default,
    val showNextCode: Boolean = false,
    val hideCodes: Boolean = false,
    val events: List<HomeUiEvent> = listOf(),
    val items: List<HomeListItem> = mutableListOf(),
) {
    fun getService(id: Long): Service? {
        return services.firstOrNull { it.id == id }
    }
}

sealed interface HomeUiEvent {
    data object ShowQrFromGalleryDialog : HomeUiEvent
    data class ServiceAdded(val id: Long) : HomeUiEvent
    data class OpenImport(val filePath: String) : HomeUiEvent
}