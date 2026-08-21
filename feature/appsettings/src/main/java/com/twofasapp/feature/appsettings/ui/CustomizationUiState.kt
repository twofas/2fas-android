package com.twofasapp.feature.appsettings.ui

import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle

internal data class CustomizationUiState(
    val selectedTheme: SelectedTheme = SelectedTheme.Auto,
    val dynamicColors: Boolean = false,
    val servicesStyle: ServicesStyle = ServicesStyle.Default,
    val showNextCode: Boolean = false,
    val hideCodes: Boolean = false,
    val autoFocusSearch: Boolean = false,
    val showBackupNotice: Boolean = true,
)