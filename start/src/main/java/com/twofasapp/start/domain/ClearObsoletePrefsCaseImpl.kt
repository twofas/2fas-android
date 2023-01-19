package com.twofasapp.start.domain

import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.PlainPreferences

class ClearObsoletePrefsCaseImpl(
    private val preferencesPlain: PlainPreferences,
    private val preferencesEncrypted: EncryptedPreferences,
) : ClearObsoletePrefsCase {

    override suspend fun invoke() {
        preferencesPlain.delete("session")
        preferencesPlain.delete("currentDevice")
    }
}