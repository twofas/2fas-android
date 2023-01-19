package com.twofasapp.data.session.local

import com.twofasapp.storage.PlainPreferences

class SessionLocalSourceImpl(private val preferences: PlainPreferences) : SessionLocalSource {

    companion object {
        private const val KeyShowOnboardWarning = "showOnboardWarning"
    }

    override suspend fun isOnboardingDisplayed(): Boolean {
        return preferences.getBoolean(KeyShowOnboardWarning)?.not() ?: false
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        preferences.putBoolean(KeyShowOnboardWarning, isDisplayed.not())
    }
}