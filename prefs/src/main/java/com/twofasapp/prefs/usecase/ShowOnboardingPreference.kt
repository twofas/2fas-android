package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceBoolean
import com.twofasapp.storage.Preferences

class ShowOnboardingPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "showOnboardWarning"
    override val default: Boolean = true
}