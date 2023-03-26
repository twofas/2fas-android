package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceLong
import com.twofasapp.storage.Preferences

class AppUpdateLastCheckVersionPreference(preferences: Preferences) : PreferenceLong(preferences) {
    override val key: String = "appUpdateLastCheckVersion"
}