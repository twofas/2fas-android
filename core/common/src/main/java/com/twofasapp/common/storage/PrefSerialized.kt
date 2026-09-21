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

class PrefSerialized<T>(
    owner: DataStoreOwner,
    keyName: String,
    encrypted: Boolean,
    private val serializer: KSerializer<T>,
    private val default: T,
) : BasePref<String, T>(
    owner = owner,
    keyType = KeyType.String,
    keyName = keyName,
    encrypted = encrypted,
) {
    override suspend fun set(value: T) {
        super.setInternal(
            owner.json.encodeToString(serializer, value),
        )
    }

    override fun asFlow(): Flow<T> {
        return asFlowInternal().map { string ->
            try {
                string?.let { owner.json.decodeFromString(serializer, it) } ?: default
            } catch (e: Exception) {
                default
            }
        }
    }
}