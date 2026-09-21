/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.storage

import kotlinx.coroutines.flow.Flow

class PrefNullable<T>(
    owner: DataStoreOwner,
    keyName: String,
    keyType: KeyType,
    encrypted: Boolean,
) : BasePref<T, T?>(
    owner = owner,
    keyType = keyType,
    keyName = keyName,
    encrypted = encrypted,
) {
    override suspend fun set(value: T?) {
        super.setInternal(value)
    }

    override fun asFlow(): Flow<T?> {
        return asFlowInternal()
    }
}