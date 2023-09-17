package com.twofasapp.usecases

import com.twofasapp.prefs.usecase.ShowOnboardingPreference

internal class GetShowOnboardingCaseImpl(
    private val showOnboardingPreference: ShowOnboardingPreference,
) : GetShowOnboardingCase {

    override fun invoke(): Boolean {
        return showOnboardingPreference.get()
    }
}