package com.twofasapp.storage.internal

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal class EncryptedSharedPreferencesFactory(private val context: Context) : SharedPreferencesFactory {

    override fun create(): SharedPreferences {
        val mainKey = MasterKey.Builder(context)
            .setUserAuthenticationRequired(false)
            .setRequestStrongBoxBacked(false)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "${context.packageName}_preferences_encrypted",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}