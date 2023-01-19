package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceBoolean

class ShowNextTokenPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "showNextToken"
}