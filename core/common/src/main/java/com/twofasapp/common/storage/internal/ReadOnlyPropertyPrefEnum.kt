/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage.internal

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.PrefEnum
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ReadOnlyPropertyPrefEnum<T : Enum<*>>(
    private val keyName: String?,
    private val encrypted: Boolean,
    private val cls: Class<T>,
    private val default: T,
) : ReadOnlyProperty<DataStoreOwner, PrefEnum<T>> {

    private var cache: PrefEnum<T>? = null

    override fun getValue(thisRef: DataStoreOwner, property: KProperty<*>): PrefEnum<T> {
        return cache ?: PrefEnum(
            owner = thisRef,
            keyName = keyName ?: property.name,
            encrypted = encrypted,
            cls = cls,
            default = default,
        ).also { cache = it }
    }
}