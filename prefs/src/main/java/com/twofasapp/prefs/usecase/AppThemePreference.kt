package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceBoolean
import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceModel

@Deprecated("Remove - old appThemeKey")
class AppThemePreference(preferences: Preferences) : PreferenceBoolean(preferences) {

    override val key: String = "appTheme"
    override val default: Boolean = true
}