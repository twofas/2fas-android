package com.twofasapp.feature.appsettings.ui

import com.twofasapp.data.session.domain.SelectedTheme

data class AppSettingsUiState(
    val showNextToken: Boolean = false,
    val selectedTheme: SelectedTheme = SelectedTheme.Auto,
)