package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceString
import com.twofasapp.storage.Preferences

class DatabaseMasterKeyPreference(preferences: Preferences) : PreferenceString(preferences) {
    override val key: String = "databaseMasterKey"
    override val default: String = ""
}