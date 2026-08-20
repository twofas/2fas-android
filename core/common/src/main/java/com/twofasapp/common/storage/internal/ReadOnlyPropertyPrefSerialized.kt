/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage.internal

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.PrefSerialized
import kotlinx.serialization.KSerializer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ReadOnlyPropertyPrefSerialized<T>(
    private val keyName: String?,
    private val encrypted: Boolean,
    private val serializer: KSerializer<T>,
    private val default: T,
) : ReadOnlyProperty<DataStoreOwner, PrefSerialized<T>> {

    private var cache: PrefSerialized<T>? = null

    override fun getValue(thisRef: DataStoreOwner, property: KProperty<*>): PrefSerialized<T> {
        return cache ?: PrefSerialized(
            owner = thisRef,
            keyName = keyName ?: property.name,
            encrypted = encrypted,
            serializer = serializer,
            default = default,
        ).also { cache = it }
    }
}