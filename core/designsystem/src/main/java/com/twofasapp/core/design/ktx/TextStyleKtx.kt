/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * Returns a copy of this style with fontSize and lineHeight anchored to dp instead of sp,
 * so the text does not scale with the user's system font size setting.
 */
@Composable
fun TextStyle.fixedFontSize(): TextStyle {
    val density = LocalDensity.current
    return remember(this, density) {
        with(density) {
            copy(
                fontSize = fontSize.value.dp.toSp(),
                lineHeight = lineHeight.value.dp.toSp(),
            )
        }
    }
}