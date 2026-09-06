/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.android.viewmodel

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object LocalViewModelStoreOwnersHolder {
    private val lock = ReentrantLock()
    private val storeOwnerMap: MutableMap<String, ViewModelStoreOwner> = mutableMapOf()
    private val refCounts: MutableMap<String, Int> = mutableMapOf()

    fun getOwner(key: String): ViewModelStoreOwner = lock.withLock {
        refCounts[key] = (refCounts[key] ?: 0) + 1
        storeOwnerMap.getOrPut(key) {
            object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            }
        }
    }

    fun remove(key: String, clearStore: Boolean) = lock.withLock {
        // Refcounted: nav entries sharing one owner overlap during transitions,
        // so the store is cleared only when the last holder releases it.
        val count = (refCounts[key] ?: 1) - 1
        if (count > 0) {
            refCounts[key] = count
            return@withLock
        }
        refCounts.remove(key)
        // On configuration change the store is kept so the recreated
        // composition can re-acquire it.
        if (clearStore) {
            storeOwnerMap[key]?.viewModelStore?.clear()
            storeOwnerMap.remove(key)
        }
    }
}