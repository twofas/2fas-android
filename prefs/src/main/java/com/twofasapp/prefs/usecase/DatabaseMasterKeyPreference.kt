package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceString

class DatabaseMasterKeyPreference(preferences: Preferences) : PreferenceString(preferences) {
    override val key: String = "databaseMasterKey"
    override val default: String = ""
}