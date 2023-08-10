package com.twofasapp.data.session.domain

data class AppSettings(
    val showNextCode: Boolean = false,
    val autoFocusSearch: Boolean = false,
    val showBackupNotice: Boolean = false,
    val sendCrashLogs: Boolean = false,
    val allowScreenshots: Boolean = false,
    val selectedTheme: SelectedTheme = SelectedTheme.Auto,
    val servicesStyle: ServicesStyle = ServicesStyle.Default,
    val servicesSort: ServicesSort = ServicesSort.Manual,
    val hideCodes: Boolean = false,
)
