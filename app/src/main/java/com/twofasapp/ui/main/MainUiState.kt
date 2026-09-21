package com.twofasapp.ui.main

import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.common.environment.BuildVariant

internal data class MainUiState(
    val buildVariant: BuildVariant = BuildVariant.Release,
    val selectedTheme: SelectedTheme? = null,
    val dynamicColors: Boolean = false,
    val startDestination: StartDestination? = null,
    val browserExtRequests: List<BrowserExtRequest> = emptyList(),
    val addServiceAdvancedExpanded: Boolean = false,
    val showBackupError: Boolean = false,
    val events: List<MainUiEvent> = emptyList(),
) {
    enum class StartDestination {
        Home, Onboarding
    }
}

internal sealed interface MainUiEvent