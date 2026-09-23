package com.twofasapp.migration

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.KeyType
import com.twofasapp.common.storage.PrefNullable
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.ByteArrayInputStream
import java.io.File
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher
import javax.crypto.CipherInputStream

class MigrateDataStore(
    private val context: Context,
    private val dataStoreOwner: DataStoreOwner,
) {
    suspend fun invoke() {
        if (isMigrated()) return

        migratePlain()
        migrateSecureStorage()
        migrateEncrypted()

        setMigrated()
    }

    private suspend fun migratePlain() {
        val fileName = "${context.packageName}_preferences"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val entries = sharedPreferences.all

        dataStoreOwner.dataStore.edit { preferences ->
            entries.forEach { (key, value) ->
                when (key) {
                    "showOnboardWarning" -> preferences[booleanPreferencesKey("onboardingDisplayed")] = (value as Boolean).not()
                    "showNextToken" -> preferences[booleanPreferencesKey("showNextToken")] = value as Boolean
                    "showBackupNotice" -> preferences[booleanPreferencesKey("showBackupNotice")] = value as Boolean
                    "autoFocusSearch" -> preferences[booleanPreferencesKey("autoFocusSearch")] = value as Boolean
                    "sendCrashLogs" -> preferences[booleanPreferencesKey("sendCrashLogs")] = value as Boolean
                    "allowScreenshots" -> preferences[booleanPreferencesKey("allowScreenshots")] = value as Boolean
                    "hideCodes" -> preferences[booleanPreferencesKey("hideCodes")] = value as Boolean
                    "dynamicColors" -> preferences[booleanPreferencesKey("dynamicColors")] = value as Boolean
                    "selectedTheme" -> preferences[stringPreferencesKey("selectedTheme")] = value as String
                    "servicesStyle" -> {
                        // Legacy styles shifted by one: old "Default" is now "Large", old "Compact" is now "Default".
                        val servicesStyle = when (value as String) {
                            "Compact" -> ServicesStyle.Default
                            else -> ServicesStyle.Large
                        }

                        preferences[stringPreferencesKey("servicesStyle")] = servicesStyle.name
                    }

                    "servicesSort" -> preferences[stringPreferencesKey("servicesSort")] = value as String

                    "periodicNotificationCounter" -> preferences[intPreferencesKey("periodicNotificationCounter")] = value as Int
                    "periodicNotificationTimestamp" -> preferences[longPreferencesKey("periodicNotificationTimestamp")] = value as Long

                    "timeDelta" -> preferences[longPreferencesKey("timeDelta")] = value as Long

                    "appInstallTimestamp" -> preferences[longPreferencesKey("appInstallTimestamp")] = value as Long
                    "noCompanionAppFromTimestamp" -> preferences[longPreferencesKey("noCompanionAppFromTimestamp")] = value as Long
                    "passBannerDismissTimestamp" -> preferences[longPreferencesKey("passBannerDismissTimestamp")] = value as Long
                    "appReviewPromptedTimestamp" -> preferences[longPreferencesKey("appReviewPromptedTimestamp")] = value as Long

                    "appUpdateLastCheckVersion" -> preferences[longPreferencesKey("appUpdateLastCheckVersion")] = value as Long
                    "currentAppVersionCode" -> preferences[longPreferencesKey("currentAppVersionCode")] = value as Long

                    "servicesOrder" -> preferences[stringPreferencesKey("servicesOrder")] = value as String
                    "groups" -> preferences[stringPreferencesKey("groups")] = value as String
                    "widgetSettings" -> preferences[stringPreferencesKey("widgetSettings")] = value as String

                    else -> return@forEach
                }
            }

            if (entries.containsKey("servicesStyle").not()) {
                // Existing users without an explicit choice had the legacy default look, which is now "Large".
                preferences[stringPreferencesKey("servicesStyle")] = ServicesStyle.Large.name
            }
        }

        // Encrypted values go through BasePref, which runs its own transaction, so they are written after the edit.
        (entries["lockStatus"] as? String)?.let { lockStatus ->
            val lockMethod = when (lockStatus) {
                "NO_LOCK" -> "NoLock"
                "PIN_LOCK", "PIN_SECURED" -> "Pin"
                "FINGERPRINT_LOCK", "FINGERPRINT_WITH_PIN_SECURED" -> "Biometrics"
                else -> "NoLock"
            }

            writeEncrypted("lockMethod", lockMethod)
        }

        (entries["mobileDevice"] as? String)?.let { writeEncrypted("mobileDevice", it) }
        (entries["remoteBackupStatus"] as? String)?.let { writeEncrypted("remoteBackupStatus", it) }
    }

    private suspend fun migrateEncrypted() {
        val fileName = "${context.packageName}_preferences_encrypted"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        val masterKey = MasterKey.Builder(context)
            .setUserAuthenticationRequired(false)
            .setRequestStrongBoxBacked(false)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        val encryptedKeys = listOf(
            "recentlyDeleted",
            "pinOptions",
            "invalidPinStatus",
            "remoteBackupKey",
            "databaseMasterKey",
        )

        encryptedKeys.forEach { key -> migrateEncryptedKey(sharedPreferences, key) }
    }

    private suspend fun migrateEncryptedKey(source: SharedPreferences, key: String) {
        val value = source.getString(key, null) ?: return

        writeEncrypted(key, value)
    }

    private suspend fun migrateSecureStorage() {
        // Historically "pinSecured" was stored via the adorsys SecureStorage library. Since minSdk = 23 the
        // value was always RSA-encrypted (RSA/ECB/PKCS1Padding, alias "adorsysKeyPair") and kept, Base64
        // encoded, in a plain SharedPreferences file named "SecurePreferences" under its literal key.
        val fileName = "SecurePreferences"
        val key = "pinSecured"

        if (File(context.dataDir, "shared_prefs/$fileName.xml").exists().not()) return

        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val storedValue = sharedPreferences.getString(key, null)

        if (storedValue.isNullOrBlank()) return

        val value = decryptSecureStorageValue(storedValue)

        writeEncrypted(key, value)
    }

    private suspend fun writeEncrypted(key: String, value: String) {
        PrefNullable<String>(
            owner = dataStoreOwner,
            keyName = key,
            keyType = KeyType.String,
            encrypted = true,
        ).set(value)
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

    private suspend fun isMigrated(): Boolean {
        return dataStoreOwner.dataStore.data.map { it[MigratedFlag] ?: false }.first()
    }

    private suspend fun setMigrated() {
        dataStoreOwner.dataStore.edit { it[MigratedFlag] = true }
    }

    companion object {
        private val MigratedFlag = booleanPreferencesKey("migratedToDataStore")
    }
}