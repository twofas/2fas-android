package com.twofasapp.data.session.domain

data class AppSettings(
    val showNextCode: Boolean = false,
    val autoFocusSearch: Boolean = false,
    val selectedTheme: SelectedTheme = SelectedTheme.Auto,
    val servicesStyle: ServicesStyle = ServicesStyle.Default,
)
