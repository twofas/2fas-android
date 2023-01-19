package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceBoolean

class ShowOnboardingPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "showOnboardWarning"
    override val default: Boolean = true
}