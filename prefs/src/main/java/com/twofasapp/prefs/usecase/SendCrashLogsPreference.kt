package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceBoolean
import com.twofasapp.storage.Preferences

class SendCrashLogsPreference(preferences: Preferences) : PreferenceBoolean(preferences) {
    override val key: String = "sendCrashLogs"
    override val default: Boolean = true
}