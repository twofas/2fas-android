package com.twofasapp.navigation

import android.app.Activity
import com.twofasapp.extensions.startActivity
import com.twofasapp.features.main.MainServicesActivity
import com.twofasapp.start.ui.onboarding.OnboardingActivity

class StartRouterImpl(
    private val activity: Activity
) : StartRouter() {

    override fun navigateBack() = activity.onBackPressed()

    override fun navigate(direction: StartDirections) {
        when (direction) {
            is StartDirections.Main -> activity.startActivity<MainServicesActivity>()
            is StartDirections.Onboarding -> activity.startActivity<OnboardingActivity>()
        }
    }
}