package com.twofasapp.feature.startup.ui.startup

import androidx.lifecycle.ViewModel
import com.twofasapp.data.session.SessionRepository

class StartupViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    suspend fun finishOnboarding() {
        sessionRepository.setOnboardingDisplayed(true)
    }
}