package com.twofasapp.data.session

interface SessionRepository {
    suspend fun isOnboardingDisplayed(): Boolean
    suspend fun setOnboardingDisplayed(isDisplayed: Boolean)
}