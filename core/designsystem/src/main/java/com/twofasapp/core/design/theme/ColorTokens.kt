/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.twofasapp.core.design.MdtTheme

@Immutable
data class ColorTokens(
    val seed: Color = Color.Unspecified,
    val primary: Color = Color.Unspecified,
    val onPrimary: Color = Color.Unspecified,
    val primaryContainer: Color = Color.Unspecified,
    val onPrimaryContainer: Color = Color.Unspecified,
    val secondary: Color = Color.Unspecified,
    val onSecondary: Color = Color.Unspecified,
    val secondaryContainer: Color = Color.Unspecified,
    val onSecondaryContainer: Color = Color.Unspecified,
    val tertiary: Color = Color.Unspecified,
    val onTertiary: Color = Color.Unspecified,
    val tertiaryContainer: Color = Color.Unspecified,
    val onTertiaryContainer: Color = Color.Unspecified,
    val error: Color = Color.Unspecified,
    val onError: Color = Color.Unspecified,
    val errorContainer: Color = Color.Unspecified,
    val onErrorContainer: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
    val onBackground: Color = Color.Unspecified,
    val surface: Color = Color.Unspecified,
    val onSurface: Color = Color.Unspecified,
    val surfaceVariant: Color = Color.Unspecified,
    val onSurfaceVariant: Color = Color.Unspecified,
    val outline: Color = Color.Unspecified,
    val outlineVariant: Color = Color.Unspecified,
    val scrim: Color = Color.Unspecified,
    val inverseSurface: Color = Color.Unspecified,
    val inverseOnSurface: Color = Color.Unspecified,
    val inversePrimary: Color = Color.Unspecified,
    val surfaceDim: Color = Color.Unspecified,
    val surfaceBright: Color = Color.Unspecified,
    val surfaceContainerLowest: Color = Color.Unspecified,
    val surfaceContainerLow: Color = Color.Unspecified,
    val surfaceContainer: Color = Color.Unspecified,
    val surfaceContainerHigh: Color = Color.Unspecified,
    val surfaceContainerHighest: Color = Color.Unspecified,
    val transparent: Color = Color.Transparent,
    val onSurface08: Color = onSurface.copy(alpha = 0.08f),
    val onSurface12: Color = onSurface.copy(alpha = 0.12f),
    val onSurface16: Color = onSurface.copy(alpha = 0.16f),
    val onSurface20: Color = onSurface.copy(alpha = 0.20f),
    val onSurface24: Color = onSurface.copy(alpha = 0.24f),
    val onSurface28: Color = onSurface.copy(alpha = 0.28f),

    val accentLightBlue: Color = Color.Unspecified,
    val accentIndigo: Color = Color.Unspecified,
    val accentPurple: Color = Color.Unspecified,
    val accentTurquoise: Color = Color.Unspecified,
    val accentGreen: Color = Color.Unspecified,
    val accentOrange: Color = Color.Unspecified,
    val accentYellow: Color = Color.Unspecified,
    val accentPink: Color = Color.Unspecified,
    val accentBrown: Color = Color.Unspecified,
) {
    val divider: Color
        get() = surfaceVariant

    val iconTint: Color
        get() = onSurfaceVariant

    val accentRed: Color
        get() = primary
}

@Composable
fun isDarkTheme() = MdtTheme.color.background.luminance() < 0.5