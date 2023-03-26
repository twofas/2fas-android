package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceBoolean
import com.twofasapp.storage.Preferences

class ShowNextTokenPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "showNextToken"
}