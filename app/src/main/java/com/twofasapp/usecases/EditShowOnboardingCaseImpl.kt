package com.twofasapp.usecases

import com.twofasapp.prefs.usecase.ShowOnboardingPreference

internal class EditShowOnboardingCaseImpl(
    private val showOnboardingPreference: ShowOnboardingPreference,
) : EditShowOnboardingCase {

    override fun invoke(shouldShowOnboarding: Boolean) {
        showOnboardingPreference.put(shouldShowOnboarding)
    }
}