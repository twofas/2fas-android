package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceBoolean

class MigratedToRoomPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "migratedToRoom"
}