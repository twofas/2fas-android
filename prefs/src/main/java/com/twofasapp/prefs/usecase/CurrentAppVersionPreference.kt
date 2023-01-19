package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceLong

class CurrentAppVersionPreference(preferences: Preferences) : PreferenceLong(preferences) {
    override val key: String = "currentAppVersionCode"
}