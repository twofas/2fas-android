/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.android.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.twofasapp.android.ktx.currentActivity
import com.twofasapp.common.crypto.Uuid

@Composable
fun ProvidesViewModelStoreOwner(
    ownerKey: String = rememberSaveable { Uuid.generate() },
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.currentActivity

    remember {
        object : RememberObserver {
            override fun onRemembered() = Unit
            override fun onAbandoned() {
                clear()
            }

            override fun onForgotten() {
                clear()
            }

            private fun clear() {
                val isChangingConfigurations = activity.isChangingConfigurations
                if (!isChangingConfigurations) {
                    LocalViewModelStoreOwnersHolder.remove(ownerKey)
                }
            }
        }
    }

    val viewModelStoreOwner: ViewModelStoreOwner = remember(ownerKey) {
        LocalViewModelStoreOwnersHolder.getOwner(ownerKey)
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        content()
    }
}