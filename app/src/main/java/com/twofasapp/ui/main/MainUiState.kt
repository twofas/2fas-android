package com.twofasapp.ui.main

import com.twofasapp.common.domain.SelectedTheme

internal data class MainUiState(
    val selectedTheme: SelectedTheme? = null,
    val startDestination: StartDestination? = null,
    val browserExtRequests: List<BrowserExtRequest> = emptyList(),
    val addServiceAdvancedExpanded: Boolean = false,
    val events: List<MainUiEvent> = emptyList(),
) {
    enum class StartDestination {
        Home, Onboarding
    }
}

internal sealed interface MainUiEvent
