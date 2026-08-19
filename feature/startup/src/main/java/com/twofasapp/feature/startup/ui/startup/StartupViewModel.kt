package com.twofasapp.feature.startup.ui.startup

import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SessionRepository

class StartupViewModel(
    private val sessionRepository: SessionRepository,
    private val navigator: Navigator,
) : ViewModel() {

    fun finishOnboarding() {
        launchScoped {
            sessionRepository.setOnboardingDisplayed(true)
            navigator.resetTo(Screen.Services)
        }
    }
}