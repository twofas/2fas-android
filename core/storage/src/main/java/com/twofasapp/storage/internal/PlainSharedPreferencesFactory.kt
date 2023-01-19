package com.twofasapp.storage.internal

import android.content.Context
import android.content.SharedPreferences

internal class PlainSharedPreferencesFactory(private val context: Context) : SharedPreferencesFactory {

    override fun create(): SharedPreferences =
        context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
}