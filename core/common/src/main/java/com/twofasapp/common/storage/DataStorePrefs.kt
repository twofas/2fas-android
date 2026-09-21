/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

@file:JvmName("PrefKt")

package com.twofasapp.common.storage

import com.twofasapp.common.storage.internal.ReadOnlyPropertyPref
import com.twofasapp.common.storage.internal.ReadOnlyPropertyPrefEnum
import com.twofasapp.common.storage.internal.ReadOnlyPropertyPrefEnumNullable
import com.twofasapp.common.storage.internal.ReadOnlyPropertyPrefNullable
import com.twofasapp.common.storage.internal.ReadOnlyPropertyPrefSerialized
import com.twofasapp.common.storage.internal.ReadOnlyPropertyPrefSerializedNullable
import kotlinx.serialization.KSerializer
import kotlin.properties.ReadOnlyProperty

internal val Tag = "Prefs"

/**
 * Prefs non-null
 */
fun intPref(default: Int, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<Int>> =
    ReadOnlyPropertyPref(keyType = KeyType.Int, keyName = name, encrypted = encrypted, default = default)

fun doublePref(default: Double, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<Double>> =
    ReadOnlyPropertyPref(keyType = KeyType.Double, keyName = name, encrypted = encrypted, default = default)

fun longPref(default: Long, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<Long>> =
    ReadOnlyPropertyPref(keyType = KeyType.Long, keyName = name, encrypted = encrypted, default = default)

fun floatPref(default: Float, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<Float>> =
    ReadOnlyPropertyPref(keyType = KeyType.Float, keyName = name, encrypted = encrypted, default = default)

fun booleanPref(default: Boolean, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<Boolean>> =
    ReadOnlyPropertyPref(keyType = KeyType.Boolean, keyName = name, encrypted = encrypted, default = default)

fun bytesPref(default: ByteArray, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<ByteArray>> =
    ReadOnlyPropertyPref(keyType = KeyType.String, keyName = name, encrypted = encrypted, default = default)

fun stringPref(default: String, name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, Pref<String>> =
    ReadOnlyPropertyPref(keyType = KeyType.String, keyName = name, encrypted = encrypted, default = default)

fun <T : Enum<*>> enumPref(
    cls: Class<T>,
    default: T,
    name: String? = null,
    encrypted: Boolean = false,
): ReadOnlyProperty<DataStoreOwner, PrefEnum<T>> =
    ReadOnlyPropertyPrefEnum(keyName = name, encrypted = encrypted, cls = cls, default = default)

fun <T> serializedPref(
    serializer: KSerializer<T>,
    default: T,
    name: String? = null,
    encrypted: Boolean = false,
): ReadOnlyProperty<DataStoreOwner, PrefSerialized<T>> =
    ReadOnlyPropertyPrefSerialized(keyName = name, encrypted = encrypted, serializer = serializer, default = default)

/**
 * Prefs nullable
 */
fun intPrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<Int>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.Int, keyName = name, encrypted = encrypted)

fun doublePrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<Double>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.Double, keyName = name, encrypted = encrypted)

fun longPrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<Long>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.Long, keyName = name, encrypted = encrypted)

fun floatPrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<Float>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.Float, keyName = name, encrypted = encrypted)

fun booleanPrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<Boolean>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.Boolean, keyName = name, encrypted = encrypted)

fun stringPrefNullable(name: String? = null, encrypted: Boolean = false): ReadOnlyProperty<DataStoreOwner, PrefNullable<String>> =
    ReadOnlyPropertyPrefNullable(keyType = KeyType.String, keyName = name, encrypted = encrypted)

fun <T : Enum<*>> enumPrefNullable(
    cls: Class<T>,
    name: String? = null,
    encrypted: Boolean = false,
): ReadOnlyProperty<DataStoreOwner, PrefEnumNullable<T>> =
    ReadOnlyPropertyPrefEnumNullable(keyName = name, encrypted = encrypted, cls = cls)

fun <T> serializedPrefNullable(
    serializer: KSerializer<T>,
    name: String? = null,
    encrypted: Boolean = false,
): ReadOnlyProperty<DataStoreOwner, PrefSerializedNullable<T>> =
    ReadOnlyPropertyPrefSerializedNullable(keyName = name, encrypted = encrypted, serializer = serializer)