package com.twofasapp.ui.main

sealed interface MainUiState {
    object ShowOnboarding : MainUiState
    object ShowHome : MainUiState
}
