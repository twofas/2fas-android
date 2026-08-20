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
         * KeyShowOnboardWarning = "showOnboardWarning" (Boolean)
         */

        dataStoreOwner.dataStore.edit { preferences ->
            entries.forEach { (key, value) ->
                when (key) {
                    "showOnboardWarning" -> preferences[booleanPreferencesKey("onboardingDisplayed")] = value as Boolean
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