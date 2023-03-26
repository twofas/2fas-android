package com.twofasapp.settings.ui.main

import com.twofasapp.prefs.model.AppTheme

internal data class SettingsMainUiState(
    val theme: AppTheme = AppTheme.AUTO,
    val showNextToken: Boolean = false,
    val sendCrashLogs: Boolean = true,
)