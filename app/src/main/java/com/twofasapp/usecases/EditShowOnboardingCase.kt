package com.twofasapp.usecases

internal interface EditShowOnboardingCase {
    operator fun invoke(shouldShowOnboarding: Boolean)
}