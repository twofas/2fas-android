package com.twofasapp.designsystem.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


// Light theme colors
internal val seedLight = Color(0xFFED1C24)
internal val primaryLight = Color(0xFFED1C24)
internal val backgroundLight = Color(0xFFFFFFFF)
internal val surfaceLight = Color(0xFFF9F9F9)
internal val surfaceVariantLight = Color(0xFFEEEEEE)
internal val onSurfacePrimaryLight = Color(0xFF000000)
internal val onSurfaceSecondaryLight = Color(0xFF9E9E9E)
internal val onSurfaceTertiaryLight = Color(0xFF4C4C4C)
internal val primaryIndicatorLight = Color(0xFFF8E2E3)
internal val serviceBackgroundWithGroupsLight = Color(0xFFFCFCFC)
internal val switchTrackLight = Color(0xFFEEEEEE)
internal val switchThumbLight = Color(0xFFBBBBBB)

// Dark theme colors
internal val seedDark = Color(0xFFF83A40)
internal val primaryDark = Color(0xFFF83A40)
internal val backgroundDark = Color(0xFF101116)
internal val surfaceDark = Color(0xFF1A1B21)
internal val surfaceVariantDark = Color(0xFF232323)
internal val onSurfacePrimaryDark = Color(0xFFFFFFFF)
internal val onSurfaceSecondaryDark = Color(0xFF636363)
internal val onSurfaceTertiaryDark = Color(0xFF9E9E9E)
internal val primaryIndicatorDark = Color(0xFF482227)
internal val serviceBackgroundWithGroupsDark = Color(0xFF17181B)
internal val switchTrackDark = Color(0xFF1F2025)
internal val switchThumbDark = Color(0xFF48494E)

// Light Color Scheme
internal val OverriddenLightColors = lightColorScheme(
    primary = primaryLight,
    onPrimary = onSurfacePrimaryLight,
    background = backgroundLight,
    onBackground = onSurfacePrimaryLight,
    surface = surfaceLight,
    onSurface = onSurfacePrimaryLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfacePrimaryLight,
)

// Dark Color Scheme
internal val OverriddenDarkColors = darkColorScheme(
    primary = primaryDark,
    onPrimary = onSurfacePrimaryDark,
    background = backgroundDark,
    onBackground = onSurfacePrimaryDark,
    surface = surfaceDark,
    onSurface = onSurfacePrimaryDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfacePrimaryDark,
)
