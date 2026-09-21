/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage.internal

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.KeyType
import com.twofasapp.common.storage.Pref
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ReadOnlyPropertyPref<T>(
    private val keyType: KeyType,
    private val keyName: String?,
    private val encrypted: Boolean,
    private val default: T,
) : ReadOnlyProperty<DataStoreOwner, Pref<T>> {

    private var cache: Pref<T>? = null

    override fun getValue(thisRef: DataStoreOwner, property: KProperty<*>): Pref<T> {
        return cache ?: Pref(
            owner = thisRef,
            keyType = keyType,
            keyName = keyName ?: property.name,
            encrypted = encrypted,
            default = default,
        ).also { cache = it }
    }
}