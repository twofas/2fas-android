/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
@Stable
class TypographyVariant(
    fontWeight: FontWeight,
    fontFamily: FontFamily = FontFamily.Default,
    defaultColor: Color,
) {

    val xxs = TextStyle(
        fontSize = 10.sp,
        lineHeight = 16.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val xs = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val sm = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val base = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val lg = TextStyle(
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val xl = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val xl2 = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val xl3 = TextStyle(
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )

    val xl4 = TextStyle(
        fontSize = 36.sp,
        lineHeight = 40.sp,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        color = defaultColor,
    )
}