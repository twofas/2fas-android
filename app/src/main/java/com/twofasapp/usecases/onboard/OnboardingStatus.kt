package com.twofasapp.usecases.onboard

import com.twofasapp.prefs.usecase.ShowOnboardingPreference

class OnboardingStatus(private val showOnboardingPreference: com.twofasapp.prefs.usecase.ShowOnboardingPreference) {

    fun shouldShow() = showOnboardingPreference.get()
    fun markShown() = showOnboardingPreference.put(false)
}