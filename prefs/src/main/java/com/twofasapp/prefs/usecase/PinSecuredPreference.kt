package com.twofasapp.prefs.usecase

import android.content.Context
import com.twofasapp.storage.Preferences
import com.twofasapp.prefs.internals.PreferenceString
import de.adorsys.android.securestoragelibrary.SecurePreferences

// Historically it was only saved in SecurePreferences, migrate at some point
class PinSecuredPreference(
    private val context: Context,
    preferences: Preferences
) : PreferenceString(preferences) {

    override val key: String = "pinSecured"
    override val default: String = ""

    override fun get(): String {
        return SecurePreferences.getStringValue(context, key, default) ?: default
    }

    override fun put(model: String) {
        if (model.isBlank()) {
            SecurePreferences.removeValue(context, key)
        } else {
            SecurePreferences.setValue(context, key, model)
        }
    }
}