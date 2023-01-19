package com.twofasapp.storage.internal

import android.content.SharedPreferences

internal interface SharedPreferencesFactory {
    fun create(): SharedPreferences
}