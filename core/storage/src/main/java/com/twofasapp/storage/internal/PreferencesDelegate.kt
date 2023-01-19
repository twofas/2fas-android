package com.twofasapp.storage.internal

import android.content.SharedPreferences
import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.PlainPreferences
import timber.log.Timber

internal class PreferencesDelegate(
    private val factory: SharedPreferencesFactory
) : EncryptedPreferences, PlainPreferences {

    private val sharedPrefs: SharedPreferences by lazy {
        factory.create()
    }

    override fun getBoolean(key: String): Boolean? {
        return getIfExists(key) { sharedPrefs.getBoolean(key, false) }
    }

    override fun getString(key: String): String? {
        return getIfExists(key) { sharedPrefs.getString(key, "") }
    }

    override fun getLong(key: String): Long? {
        return getIfExists(key) { sharedPrefs.getLong(key, 0L) }
    }

    override fun getInt(key: String): Int? {
        return getIfExists(key) { sharedPrefs.getInt(key, 0) }
    }

    override fun getFloat(key: String): Float? {
        return getIfExists(key) { sharedPrefs.getFloat(key, 0f) }
    }

    override fun putBoolean(key: String, value: Boolean) {
        logPut(key, value.toString())
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    override fun putString(key: String, value: String) {
        logPut(key, value)
        sharedPrefs.edit().putString(key, value).apply()
    }

    override fun putLong(key: String, value: Long) {
        logPut(key, value.toString())
        sharedPrefs.edit().putLong(key, value).apply()
    }

    override fun putInt(key: String, value: Int) {
        logPut(key, value.toString())
        sharedPrefs.edit().putInt(key, value).apply()
    }

    override fun putFloat(key: String, value: Float) {
        logPut(key, value.toString())
        sharedPrefs.edit().putFloat(key, value).apply()
    }

    override fun delete(key: String) {
        Timber.tag("Prefs").i("Delete :: $key")
        sharedPrefs.edit().remove(key).apply()
    }

    private fun <T> getIfExists(key: String, action: () -> T): T? =
        if (sharedPrefs.contains(key)) {
            action.invoke()
        } else {
            null
        }

    private fun logPut(key: String, value: String) {
        Timber.tag("Prefs").i("Put :: $key=$value")
    }
}