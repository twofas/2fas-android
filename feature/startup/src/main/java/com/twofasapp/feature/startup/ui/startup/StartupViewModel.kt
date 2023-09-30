package com.twofasapp.feature.startup.ui.startup

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SessionRepository

class StartupViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    fun onStartUsingClicked() {
        launchScoped {
            sessionRepository.setOnboardingDisplayed(true)
        }
    }
}