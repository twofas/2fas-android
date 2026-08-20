package com.twofasapp.feature.startup.ui.startup

import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.StartupRepository

class StartupViewModel(
    private val startupRepository: StartupRepository,
    private val navigator: Navigator,
) : ViewModel() {

    fun finishOnboarding(openBackup: Boolean) {
        launchScoped {
            startupRepository.setOnboardingDisplayed(true)

            navigator.resetTo(Screen.Services)

            if (openBackup) {
                navigator.navigate(Screen.Settings) // TODO: Replace with a proper backup screen when implemented
            }
        }
    }
}