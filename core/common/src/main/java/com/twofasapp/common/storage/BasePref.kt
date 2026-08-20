/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.twofasapp.common.crypto.EncryptedBytes
import com.twofasapp.common.crypto.decrypt
import com.twofasapp.common.crypto.encrypt
import com.twofasapp.common.ktx.decodeBase64
import com.twofasapp.common.ktx.decodeString
import com.twofasapp.common.ktx.encodeBase64
import com.twofasapp.common.logger.Flog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class BasePref<PrefType, ValueType>(
    protected val owner: DataStoreOwner,
    private val keyType: KeyType,
    private val keyName: String,
    private val encrypted: Boolean,
) {
    suspend fun delete() {
        Flog.tag(Tag).d("[DELETE] $keyName")
        owner.dataStore.edit { preferences -> preferences.remove(keyType.asPreferencesKey()) }
    }

    abstract suspend fun set(value: ValueType)

    abstract fun asFlow(): Flow<ValueType>

    suspend fun get(): ValueType = asFlow().first()

    @Suppress("UNCHECKED_CAST")
    protected fun asFlowInternal(): Flow<PrefType?> {
        return owner.dataStore.data.map { preferences ->
            if (encrypted) {
                preferences[stringPreferencesKey(keyName)]
                    ?.decodeBase64()
                    ?.let {
                        decrypt(
                            key = owner.androidKeyStore.appKey,
                            data = EncryptedBytes(it),
                        ).decodeString()
                    }
                    ?.let {
                        when (keyType) {
                            KeyType.Int -> it.toInt()
                            KeyType.Double -> it.toDouble()
                            KeyType.Long -> it.toLong()
                            KeyType.Float -> it.toFloat()
                            KeyType.Boolean -> it.toBoolean()
                            KeyType.String -> it
                        } as? PrefType
                    }
            } else {
                preferences[keyType.asPreferencesKey()] as? PrefType
            }
        }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

    protected suspend fun getInternal(): PrefType? {
        return asFlowInternal().first()
    }

    protected suspend fun setInternal(value: PrefType?) {
        owner.dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(keyType.asPreferencesKey()).also {
                    Flog.tag(Tag).d("[SET] $keyName = null")
                }
                return@edit
            }

            if (encrypted) {
                preferences[stringPreferencesKey(keyName)] =
                    encrypt(
                        key = owner.androidKeyStore.appKey,
                        data = value.toString().toByteArray(),
                    ).encodeBase64().also {
                        Flog.tag(Tag).d("[SET] $keyName = $value (encrypted = $it)")
                    }
            } else {
                Flog.tag(Tag).d("[SET] $keyName = $value")

                when (keyType) {
                    KeyType.Int -> preferences[intPreferencesKey(keyName)] = value as Int
                    KeyType.Double -> preferences[doublePreferencesKey(keyName)] = value as Double
                    KeyType.Long -> preferences[longPreferencesKey(keyName)] = value as Long
                    KeyType.Float -> preferences[floatPreferencesKey(keyName)] = value as Float
                    KeyType.Boolean -> preferences[booleanPreferencesKey(keyName)] = value as Boolean
                    KeyType.String -> preferences[stringPreferencesKey(keyName)] = value as String
                }
            }
        }
    }

    private fun KeyType.asPreferencesKey(): Preferences.Key<*> {
        return when (this) {
            KeyType.Int -> intPreferencesKey(keyName)
            KeyType.Double -> doublePreferencesKey(keyName)
            KeyType.Long -> longPreferencesKey(keyName)
            KeyType.Float -> floatPreferencesKey(keyName)
            KeyType.Boolean -> booleanPreferencesKey(keyName)
            KeyType.String -> stringPreferencesKey(keyName)
        }
    }
}