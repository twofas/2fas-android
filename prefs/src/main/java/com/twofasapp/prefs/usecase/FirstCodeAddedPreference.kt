package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceBoolean

class FirstCodeAddedPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "firstCodeAdded"
}