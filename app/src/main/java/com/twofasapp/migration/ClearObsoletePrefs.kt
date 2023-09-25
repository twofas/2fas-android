package com.twofasapp.migration

import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.PlainPreferences

class ClearObsoletePrefs(
    private val preferencesPlain: PlainPreferences,
    private val preferencesEncrypted: EncryptedPreferences,
) {

    suspend fun invoke() {
        preferencesPlain.delete("session")
        preferencesPlain.delete("currentDevice")
    }
}