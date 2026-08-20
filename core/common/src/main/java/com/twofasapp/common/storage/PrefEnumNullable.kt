/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage

import com.twofasapp.common.ktx.enumValueOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrefEnumNullable<T : Enum<*>>(
    owner: DataStoreOwner,
    keyName: String,
    encrypted: Boolean,
    private val cls: Class<T>,
) : BasePref<String, T?>(
    owner = owner,
    keyType = KeyType.String,
    keyName = keyName,
    encrypted = encrypted,
) {
    override suspend fun set(value: T?) {
        super.setInternal(value?.name)
    }

    override fun asFlow(): Flow<T?> {
        return asFlowInternal().map { string ->
            try {
                enumValueOrNull(cls, string)
            } catch (e: Exception) {
                null
            }
        }
    }
}