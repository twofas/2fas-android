package com.twofasapp.storage

import kotlinx.coroutines.flow.Flow

interface Preferences {
    fun getBoolean(key: String): Boolean?
    fun getString(key: String): String?
    fun getLong(key: String): Long?
    fun getInt(key: String): Int?
    fun getFloat(key: String): Float?

    fun putBoolean(key: String, value: Boolean)
    fun putString(key: String, value: String)
    fun putLong(key: String, value: Long)
    fun putInt(key: String, value: Int)
    fun putFloat(key: String, value: Float)

    fun <T> observe(key: String, default: T): Flow<T?>

    fun delete(key: String)
}