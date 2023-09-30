package com.twofasapp.feature.startup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.launch

class StartupViewModel(
    private val dispatchers: Dispatchers,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    fun onSkipClicked() {
        markOnboardingAsShown()
    }

    fun onStartUsingClicked() {
        markOnboardingAsShown()
    }

    fun onTermsClicked() {
    }

    fun onNextClicked(position: Int) {
        markOnboardingAsShown()
    }

    private fun markOnboardingAsShown() {
        viewModelScope.launch(dispatchers.io) {
            sessionRepository.setOnboardingDisplayed(true)
        }
    }
}