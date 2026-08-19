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
import androidx.compose.ui.text.font.FontWeight

@Immutable
@Stable
class TypographyTokens(private val colorTokens: ColorTokens) {
    val regular: TypographyVariant = TypographyVariant(fontWeight = FontWeight(400), defaultColor = colorTokens.onBackground)
    val medium: TypographyVariant = TypographyVariant(fontWeight = FontWeight(500), defaultColor = colorTokens.onBackground)
    val semiBold: TypographyVariant = TypographyVariant(fontWeight = FontWeight(600), defaultColor = colorTokens.onBackground)
    val bold: TypographyVariant = TypographyVariant(fontWeight = FontWeight(700), defaultColor = colorTokens.onBackground)
}