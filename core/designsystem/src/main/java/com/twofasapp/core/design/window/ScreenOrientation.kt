/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.window

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.window.core.layout.WindowWidthSizeClass
import com.twofasapp.core.design.ktx.currentActivity

/**
 * Locks the screen orientation to [compactOrientation] while this composition is active,
 * but only on compact-width windows (phones). Larger windows are left free to rotate.
 */
@Composable
fun ScreenOrientation(compactOrientation: Int) {
    if (LocalInspectionMode.current) return

    val activity = LocalContext.currentActivity
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    if (isCompact) {
        DisposableEffect(Unit) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = compactOrientation
            onDispose {
                activity.requestedOrientation = originalOrientation
            }
        }
    }
}