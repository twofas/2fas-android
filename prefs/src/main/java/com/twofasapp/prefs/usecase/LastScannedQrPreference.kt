package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceString

class LastScannedQrPreference(preferences: Preferences) : PreferenceString(preferences) {
    override val key: String = "lastScannedQr"
    override val default: String = ""
}