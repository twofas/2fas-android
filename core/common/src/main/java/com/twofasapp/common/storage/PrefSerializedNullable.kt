/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer

class PrefSerializedNullable<T>(
    owner: DataStoreOwner,
    keyName: String,
    encrypted: Boolean,
    private val serializer: KSerializer<T>,
) : BasePref<String, T?>(
    owner = owner,
    keyType = KeyType.String,
    keyName = keyName,
    encrypted = encrypted,
) {
    override suspend fun set(value: T?) {
        super.setInternal(
            value?.let { owner.json.encodeToString(serializer, it) },
        )
    }

    override fun asFlow(): Flow<T?> {
        return asFlowInternal().map { string ->
            try {
                string?.let { owner.json.decodeFromString(serializer, it) }
            } catch (e: Exception) {
                null
            }
        }
    }
}