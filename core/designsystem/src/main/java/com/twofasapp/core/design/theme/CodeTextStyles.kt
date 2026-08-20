/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.R

// OTP code display styles — custom Roboto weights with no variant-scale equivalent.
val CodeExtraLight = TextStyle(
    fontFamily = FontFamily(
        Font(resId = R.font.roboto_extra_light),
    ),
    fontSize = 44.sp,
    lineHeight = 44.sp,
)

val CodeLightSmall = TextStyle(
    fontFamily = FontFamily(
        Font(resId = R.font.roboto_light),
    ),
    fontSize = 24.sp,
    lineHeight = 32.sp,
)

val CodeLight = TextStyle(
    fontFamily = FontFamily(
        Font(resId = R.font.roboto_light),
    ),
    fontSize = 44.sp,
    lineHeight = 44.sp,
)

val CodeThin = TextStyle(
    fontFamily = FontFamily(
        Font(resId = R.font.roboto_thin),
    ),
    fontSize = 44.sp,
    lineHeight = 44.sp,
)