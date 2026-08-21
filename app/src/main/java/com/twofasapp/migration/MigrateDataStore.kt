package com.twofasapp.migration

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.twofasapp.common.storage.DataStoreOwner

class MigrateDataStore(
    private val context: Context,
    private val dataStoreOwner: DataStoreOwner,
) {
    suspend fun invoke() {
        val sharedPreferences = context.getSharedPreferences(
            "${context.packageName}_preferences",
            Context.MODE_PRIVATE,
        )

        val entries = sharedPreferences.all
        if (entries.isEmpty()) return

        /**
         * "showOnboardWarning" (Boolean)
         * "showNextToken" (Boolean)
         * "showBackupNotice" (Boolean)
         * "autoFocusSearch" (Boolean)
         * "sendCrashLogs" (Boolean)
         * "allowScreenshots" (Boolean)
         * "hideCodes" (Boolean)
         * "dynamicColors" (Boolean)
         * "selectedTheme" (String)
         * "servicesStyle" (String)
         * "servicesSort" (String)
         */

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

                    else -> {
                        when (value) {
                            is Boolean -> preferences[booleanPreferencesKey(key)] = value
                            is Int -> preferences[intPreferencesKey(key)] = value
                            is Long -> preferences[longPreferencesKey(key)] = value
                            is Float -> preferences[floatPreferencesKey(key)] = value
                            is String -> preferences[stringPreferencesKey(key)] = value
                            is Set<*> -> preferences[stringSetPreferencesKey(key)] = value.filterIsInstance<String>().toSet()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}