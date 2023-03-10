package com.twofasapp.feature.appsettings.ui

import com.twofasapp.data.session.domain.AppSettings

data class AppSettingsUiState(
    val appSettings: AppSettings = AppSettings(),
)