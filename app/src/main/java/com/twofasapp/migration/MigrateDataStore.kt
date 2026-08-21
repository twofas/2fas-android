package com.twofasapp.migration

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.twofasapp.common.crypto.AndroidKeyStore
import com.twofasapp.common.crypto.encrypt
import com.twofasapp.common.ktx.encodeBase64
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.prefs.model.LockMethodEntity
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher
import javax.crypto.CipherInputStream

class MigrateDataStore(
    private val context: Context,
    private val dataStoreOwner: DataStoreOwner,
    private val androidKeyStore: AndroidKeyStore,
) {
    suspend fun invoke() {
        migratePlain()
        migrateEncrypted()
        migrateSecureStorage()
    }

    private suspend fun migratePlain() {
        val fileName = "${context.packageName}_preferences"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        try {
            val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            val entries = sharedPreferences.all
            val migratedKeys = mutableListOf<String>()

            dataStoreOwner.dataStore.edit { preferences ->
                entries.forEach { (key, value) ->
                    when (key) {
                        "showOnboardWarning" -> preferences[booleanPreferencesKey("onboardingDisplayed")] = value as Boolean
                        "showNextToken" -> preferences[booleanPreferencesKey("showNextToken")] = value as Boolean
                        "showBackupNotice" -> preferences[booleanPreferencesKey("showBackupNotice")] = value as Boolean
                        "autoFocusSearch" -> preferences[booleanPreferencesKey("autoFocusSearch")] = value as Boolean
                        "sendCrashLogs" -> preferences[booleanPreferencesKey("sendCrashLogs")] = value as Boolean
                        "allowScreenshots" -> preferences[booleanPreferencesKey("allowScreenshots")] = value as Boolean
                        "hideCodes" -> preferences[booleanPreferencesKey("hideCodes")] = value as Boolean
                        "dynamicColors" -> preferences[booleanPreferencesKey("dynamicColors")] = value as Boolean
                        "selectedTheme" -> preferences[stringPreferencesKey("selectedTheme")] = value as String
                        "servicesStyle" -> preferences[stringPreferencesKey("servicesStyle")] = value as String
                        "servicesSort" -> preferences[stringPreferencesKey("servicesSort")] = value as String
                        "lockStatus" -> {
                            val lockMethod = when (value as String) {
                                "NO_LOCK" -> LockMethodEntity.NoLock.name
                                "PIN_LOCK", "PIN_SECURED" -> LockMethodEntity.Pin.name
                                "FINGERPRINT_LOCK", "FINGERPRINT_WITH_PIN_SECURED" -> LockMethodEntity.Biometrics.name
                                else -> LockMethodEntity.NoLock.name
                            }

                            preferences[stringPreferencesKey("lockMethod")] = encrypt(
                                key = androidKeyStore.dataStoreKey,
                                data = lockMethod.toByteArray(),
                            ).encodeBase64()
                        }

                        else -> return@forEach
                    }

                    migratedKeys += key
                }
            }

            // Migration to DataStore succeeded - remove the migrated keys one by one.
            sharedPreferences.edit().apply { migratedKeys.forEach { remove(it) } }.apply()
        } catch (e: Exception) {
            Timber.e(e, "Failed to migrate plain preferences")
            throw e
        }
    }

    private suspend fun migrateEncrypted() {
        val fileName = "${context.packageName}_preferences_encrypted"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        try {
            val masterKey = MasterKey.Builder(context)
                .setUserAuthenticationRequired(false)
                .setRequestStrongBoxBacked(false)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                /* context = */
                context,
                /* fileName = */
                fileName,
                /* masterKey = */
                masterKey,
                /* prefKeyEncryptionScheme = */
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                /* prefValueEncryptionScheme = */
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

            val encryptedKeys = listOf(
                "recentlyDeleted",
                "pinOptions",
                "invalidPinStatus",
                "databaseMasterKey",
                "remoteBackupKey",
            )

            val values = encryptedKeys
                .associateWith { sharedPreferences.getString(it, null) }
                .filterValues { it != null }

            dataStoreOwner.dataStore.edit { preferences ->
                values.forEach { (key, value) ->
                    val encryptedValue = encrypt(
                        key = androidKeyStore.dataStoreKey,
                        data = value.toString().toByteArray(),
                    ).encodeBase64()

                    preferences[stringPreferencesKey(key)] = encryptedValue
                }
            }

            context.deleteSharedPreferences(fileName)
        } catch (e: Exception) {
            Timber.e(e, "Failed to migrate encrypted preferences")
            throw e
        }
    }

    private suspend fun migrateSecureStorage() {
        // Historically "pinSecured" was stored via the adorsys SecureStorage library. Since minSdk = 23 the
        // value was always RSA-encrypted (RSA/ECB/PKCS1Padding, alias "adorsysKeyPair") and kept, Base64
        // encoded, in a plain SharedPreferences file named "SecurePreferences" under its literal key.
        val fileName = "SecurePreferences"
        val key = "pinSecured"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        try {
            val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            val storedValue = sharedPreferences.getString(key, null)

            if (storedValue.isNullOrBlank()) return

            val value = decryptSecureStorageValue(storedValue)

            dataStoreOwner.dataStore.edit { preferences ->
                val encryptedValue = encrypt(
                    key = androidKeyStore.dataStoreKey,
                    data = value.toByteArray(),
                ).encodeBase64()

                preferences[stringPreferencesKey(key)] = encryptedValue
            }

            context.deleteSharedPreferences(fileName)
        } catch (e: Exception) {
            Timber.e(e, "Failed to migrate secure storage")
            throw e
        }
    }

    private fun decryptSecureStorageValue(encryptedMessage: String): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val privateKey = keyStore.getKey("adorsysKeyPair", null) as PrivateKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val encryptedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decryptedBytes = CipherInputStream(ByteArrayInputStream(encryptedBytes), cipher)
            .use { it.readBytes() }

        return String(decryptedBytes, Charsets.UTF_8)
    }
}