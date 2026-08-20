package com.twofasapp.feature.appsettings.ui

import com.twofasapp.data.session.domain.AppSettings

internal data class CustomizationUiState(
    val appSettings: AppSettings = AppSettings(),
    val events: List<CustomizationUiEvent> = emptyList(),
)

internal sealed interface CustomizationUiEvent {
    object Recreate : CustomizationUiEvent
}