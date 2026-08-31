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

    fun getOwner(key: String): ViewModelStoreOwner = lock.withLock {
        storeOwnerMap.getOrPut(key) {
            object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            }
        }
    }

    fun remove(key: String) = lock.withLock {
        storeOwnerMap[key]?.viewModelStore?.clear()
        storeOwnerMap.remove(key)
    }
}