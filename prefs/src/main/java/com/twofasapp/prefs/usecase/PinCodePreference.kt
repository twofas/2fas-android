package com.twofasapp.prefs.usecase

import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceString

@Deprecated("Since version 2.1.10 use PinSecuredPreference")
class PinCodePreference(preferences: Preferences) : PreferenceString(preferences) {
    override val key: String = "pinCode"
    override val default: String = ""
}