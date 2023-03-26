package com.twofasapp.prefs.usecase

import com.twofasapp.prefs.internals.PreferenceString
import com.twofasapp.storage.Preferences

class LastScannedQrPreference(preferences: Preferences) : PreferenceString(preferences) {
    override val key: String = "lastScannedQr"
    override val default: String = ""
}