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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Immutable
@Stable
class TypographyVariant(
    fontSize: TextUnit,
    lineHeight: TextUnit,
    defaultColor: Color,
    fontFamily: FontFamily = FontFamily.Default,
) {

    val normal = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = FontWeight.Normal,
        color = defaultColor,
    )

    val medium = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = FontWeight.Medium,
        color = defaultColor,
    )

    val semiBold = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = FontWeight.SemiBold,
        color = defaultColor,
    )

    val bold = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = FontWeight.Bold,
        color = defaultColor,
    )
}