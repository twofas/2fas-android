package com.twofasapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val dispatchers: Dispatchers,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<MainUiState?> = MutableStateFlow(null)

    init {
        viewModelScope.launch(dispatchers.io) {
            val state = when (sessionRepository.isOnboardingDisplayed()) {
                true -> MainUiState.ShowHome
                false -> MainUiState.ShowOnboarding
            }

            uiState.update { state }
        }
    }
}