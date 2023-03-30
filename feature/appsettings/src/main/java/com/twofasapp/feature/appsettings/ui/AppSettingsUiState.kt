package com.twofasapp.feature.appsettings.ui

import com.twofasapp.data.session.domain.AppSettings

internal data class AppSettingsUiState(
    val appSettings: AppSettings = AppSettings(),
    val events: List<AppSettingsUiEvent> = emptyList()
)

internal sealed interface AppSettingsUiEvent {
    object Recreate : AppSettingsUiEvent
}