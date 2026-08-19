/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val seed = Color(0xFFED1C24)

// region Light
internal val primaryLight = Color(0xFFED1C24)
internal val onPrimaryLight = Color(0xFFFFFFFF) // added: white foreground on red brand
internal val primaryContainerLight = Color(0xFFF8E2E3) // added: from primaryIndicatorLight
internal val onPrimaryContainerLight = Color(0xFF410002) // added: best-match dark red
internal val secondaryLight = Color(0xFF4C4C4C) // added: neutral (from onSurfaceTertiaryLight)
internal val onSecondaryLight = Color(0xFFFFFFFF) // added
internal val secondaryContainerLight = Color(0xFFEEEEEE) // added: from surfaceVariantLight
internal val onSecondaryContainerLight = Color(0xFF000000) // added
internal val tertiaryLight = Color(0xFF8C49DE) // added: from accentPurple
internal val onTertiaryLight = Color(0xFFFFFFFF) // added
internal val tertiaryContainerLight = Color(0xFFF0DDFB) // added: best-match light purple
internal val onTertiaryContainerLight = Color(0xFF2C0A47) // added
internal val errorLight = Color(0xFFF83A40) // from current error
internal val onErrorLight = Color(0xFFFFFFFF) // added
internal val errorContainerLight = Color(0xFFFFDAD6) // added
internal val onErrorContainerLight = Color(0xFF410002) // added
internal val backgroundLight = Color(0xFFFFFFFF)
internal val onBackgroundLight = Color(0xFF000000) // from onSurfacePrimaryLight
internal val surfaceLight = Color(0xFFF9F9F9)
internal val onSurfaceLight = Color(0xFF000000) // from onSurfacePrimaryLight
internal val surfaceVariantLight = Color(0xFFEEEEEE)
internal val onSurfaceVariantLight = Color(0xFF9E9E9E) // from onSurfaceSecondaryLight
internal val outlineLight = Color(0xFFBBBBBB) // added: neutral outline (from switchThumbLight)
internal val outlineVariantLight = Color(0xFFEEEEEE) // added: from surfaceVariantLight (divider tone)
internal val scrimLight = Color(0xFF000000)
internal val inverseSurfaceLight = Color(0xFF1A1B21) // added: from dark surface
internal val inverseOnSurfaceLight = Color(0xFFF9F9F9) // added
internal val inversePrimaryLight = Color(0xFFF83A40) // added: from dark primary
internal val surfaceDimLight = Color(0xFFEEEEEE) // added
internal val surfaceBrightLight = Color(0xFFFFFFFF) // added
internal val surfaceContainerLowestLight = Color(0xFFFFFFFF) // added
internal val surfaceContainerLowLight = Color(0xFFF9F9F9) // added: from surfaceLight
internal val surfaceContainerLight = Color(0xFFF3F3F3) // added
internal val surfaceContainerHighLight = Color(0xFFEEEEEE) // added
internal val surfaceContainerHighestLight = Color(0xFFE8E8E8) // added
internal val successLight = Color(0xFF03BF38) // from accentGreen

// App-specific (no reference-design equivalent)
internal val onSurfaceTertiaryLight = Color(0xFF4C4C4C)
internal val primaryIndicatorLight = Color(0xFFF8E2E3)
internal val serviceBackgroundWithGroupsLight = Color(0xFFFCFCFC)
internal val switchTrackLight = Color(0xFFEEEEEE)
internal val switchThumbLight = Color(0xFFBBBBBB)
// endregion

// region Dark
internal val primaryDark = Color(0xFFF83A40)
internal val onPrimaryDark = Color(0xFFFFFFFF) // added: white foreground on red brand
internal val primaryContainerDark = Color(0xFF482227) // added: from primaryIndicatorDark
internal val onPrimaryContainerDark = Color(0xFFFFDAD6) // added
internal val secondaryDark = Color(0xFF9E9E9E) // added: neutral (from onSurfaceTertiaryDark)
internal val onSecondaryDark = Color(0xFF000000) // added
internal val secondaryContainerDark = Color(0xFF232323) // added: from surfaceVariantDark
internal val onSecondaryContainerDark = Color(0xFFFFFFFF) // added
internal val tertiaryDark = Color(0xFF8C49DE) // added: from accentPurple
internal val onTertiaryDark = Color(0xFFFFFFFF) // added
internal val tertiaryContainerDark = Color(0xFF3A1A5A) // added: best-match dark purple
internal val onTertiaryContainerDark = Color(0xFFF0DDFB) // added
internal val errorDark = Color(0xFFF83A40) // from current error
internal val onErrorDark = Color(0xFFFFFFFF) // added
internal val errorContainerDark = Color(0xFF93000A) // added
internal val onErrorContainerDark = Color(0xFFFFDAD6) // added
internal val backgroundDark = Color(0xFF101116)
internal val onBackgroundDark = Color(0xFFFFFFFF) // from onSurfacePrimaryDark
internal val surfaceDark = Color(0xFF1A1B21)
internal val onSurfaceDark = Color(0xFFFFFFFF) // from onSurfacePrimaryDark
internal val surfaceVariantDark = Color(0xFF232323)
internal val onSurfaceVariantDark = Color(0xFF636363) // from onSurfaceSecondaryDark
internal val outlineDark = Color(0xFF48494E) // added: neutral outline (from switchThumbDark)
internal val outlineVariantDark = Color(0xFF232323) // added: from surfaceVariantDark (divider tone)
internal val scrimDark = Color(0xFF000000)
internal val inverseSurfaceDark = Color(0xFFF9F9F9) // added: from light surface
internal val inverseOnSurfaceDark = Color(0xFF1A1B21) // added
internal val inversePrimaryDark = Color(0xFFED1C24) // added: from light primary
internal val surfaceDimDark = Color(0xFF101116) // added
internal val surfaceBrightDark = Color(0xFF33343E) // added
internal val surfaceContainerLowestDark = Color(0xFF0C0D11) // added
internal val surfaceContainerLowDark = Color(0xFF17181B) // added: from serviceBackgroundWithGroupsDark
internal val surfaceContainerDark = Color(0xFF1A1B21) // added: from surfaceDark
internal val surfaceContainerHighDark = Color(0xFF232323) // added
internal val surfaceContainerHighestDark = Color(0xFF2D2E33) // added
internal val successDark = Color(0xFF03BF38) // from accentGreen

// App-specific (no reference-design equivalent)
internal val onSurfaceTertiaryDark = Color(0xFF9E9E9E)
internal val primaryIndicatorDark = Color(0xFF482227)
internal val serviceBackgroundWithGroupsDark = Color(0xFF17181B)
internal val switchTrackDark = Color(0xFF1F2025)
internal val switchThumbDark = Color(0xFF48494E)
// endregion

// region Accents (theme-independent; accentRed follows the brand primary)
internal val accentLightBlue = Color(0xFF7F9CFF)
internal val accentIndigo = Color(0xFF5E5CE6)
internal val accentPurple = Color(0xFF8C49DE)
internal val accentTurquoise = Color(0xFF2FCFBC)
internal val accentGreen = Color(0xFF03BF38)
internal val accentOrange = Color(0xFFFF7A00)
internal val accentYellow = Color(0xFFFFBA0A)
internal val accentPink = Color(0xFFCA49DE)
internal val accentBrown = Color(0xFFBD8857)
// endregion

internal val LightColors = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

internal val DarkColors = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)