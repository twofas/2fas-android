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

@Immutable
@Stable
class TypographyTokens(colorTokens: ColorTokens) {

    val xs5 = TypographyVariant(
        fontSize = TypographySizes.xs5.fontSize,
        lineHeight = TypographySizes.xs5.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xs4 = TypographyVariant(
        fontSize = TypographySizes.xs4.fontSize,
        lineHeight = TypographySizes.xs4.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xs3 = TypographyVariant(
        fontSize = TypographySizes.xs3.fontSize,
        lineHeight = TypographySizes.xs3.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xs2 = TypographyVariant(
        fontSize = TypographySizes.xs2.fontSize,
        lineHeight = TypographySizes.xs2.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xs = TypographyVariant(
        fontSize = TypographySizes.xs.fontSize,
        lineHeight = TypographySizes.xs.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val sm = TypographyVariant(
        fontSize = TypographySizes.sm.fontSize,
        lineHeight = TypographySizes.sm.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val base = TypographyVariant(
        fontSize = TypographySizes.base.fontSize,
        lineHeight = TypographySizes.base.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val lg = TypographyVariant(
        fontSize = TypographySizes.lg.fontSize,
        lineHeight = TypographySizes.lg.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xl = TypographyVariant(
        fontSize = TypographySizes.xl.fontSize,
        lineHeight = TypographySizes.xl.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xl2 = TypographyVariant(
        fontSize = TypographySizes.xl2.fontSize,
        lineHeight = TypographySizes.xl2.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xl3 = TypographyVariant(
        fontSize = TypographySizes.xl3.fontSize,
        lineHeight = TypographySizes.xl3.lineHeight,
        defaultColor = colorTokens.onBackground,
    )

    val xl4 = TypographyVariant(
        fontSize = TypographySizes.xl4.fontSize,
        lineHeight = TypographySizes.xl4.lineHeight,
        defaultColor = colorTokens.onBackground,
    )
}