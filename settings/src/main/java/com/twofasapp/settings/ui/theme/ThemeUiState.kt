package com.twofasapp.settings.ui.theme

import com.twofasapp.prefs.model.AppTheme

internal data class ThemeUiState(
    val theme: AppTheme = AppTheme.AUTO,
    val recreateActivity: Boolean = false,
)