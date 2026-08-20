package com.twofasapp.data.session

interface StartupRepository {
    suspend fun isOnboardingDisplayed(): Boolean
    suspend fun setOnboardingDisplayed(isDisplayed: Boolean)
}