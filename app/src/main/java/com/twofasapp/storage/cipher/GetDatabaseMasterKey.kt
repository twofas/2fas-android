package com.twofasapp.storage.cipher

import com.twofasapp.prefs.usecase.DatabaseMasterKeyPreference

class GetDatabaseMasterKey(
    private val databaseMasterKeyPreference: DatabaseMasterKeyPreference,
    private val databaseKeyGenerator: DatabaseKeyGenerator,
) {

    fun execute(): String {
        val key = databaseMasterKeyPreference.get()

        if (key.isBlank()) {
            val masterKey = databaseKeyGenerator.generate(32)
            databaseMasterKeyPreference.put(masterKey)
            return masterKey
        }

        return key
    }
}