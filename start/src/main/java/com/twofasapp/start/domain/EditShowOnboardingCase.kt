package com.twofasapp.start.domain

internal interface EditShowOnboardingCase {
    operator fun invoke(shouldShowOnboarding: Boolean)
}