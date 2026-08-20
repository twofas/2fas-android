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

class Pref<T>(
    owner: DataStoreOwner,
    keyType: KeyType,
    keyName: String,
    encrypted: Boolean,
    private val default: T,
) : BasePref<T, T>(
    owner = owner,
    keyType = keyType,
    keyName = keyName,
    encrypted = encrypted,
) {
    override suspend fun set(value: T) {
        setInternal(value)
    }

    override fun asFlow(): Flow<T> {
        return asFlowInternal().map { it ?: default }
    }
}