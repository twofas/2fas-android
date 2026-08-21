package com.twofasapp.data.session.domain

data class AppSettings(
    val showBackupNotice: Boolean = false,
    val sendCrashLogs: Boolean = false,
    val allowScreenshots: Boolean = false,
)