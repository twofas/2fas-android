package com.twofasapp.data.session.local

interface SessionLocalSource {
    suspend fun isOnboardingDisplayed(): Boolean
    suspend fun setOnboardingDisplayed(isDisplayed: Boolean)
}