package com.twofasapp.data.session.local

import com.twofasapp.storage.PlainPreferences

internal class SessionLocalSource(private val preferences: PlainPreferences) {

    companion object {
        private const val KeyShowOnboardWarning = "showOnboardWarning"
    }

    suspend fun isOnboardingDisplayed(): Boolean {
        return preferences.getBoolean(KeyShowOnboardWarning)?.not() ?: false
    }

    suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        preferences.putBoolean(KeyShowOnboardWarning, isDisplayed.not())
    }
}