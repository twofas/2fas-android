/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
@Stable
class TypographyOtp(colorTokens: ColorTokens) {

    private val fontFamily = FontFamily.Roboto

    // Thin / ExtraLight / Light

    val default = TextStyle(
        fontSize = 32.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Light,
        color = colorTokens.onSurface,
    )

    val compact = TextStyle(
        fontFamily = fontFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Light,
        color = colorTokens.onSurface,
    )
}