/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.core.design.theme.ColorTokens
import com.twofasapp.core.design.theme.DarkColors
import com.twofasapp.core.design.theme.LightColors
import com.twofasapp.core.design.theme.accentBrown
import com.twofasapp.core.design.theme.accentGreen
import com.twofasapp.core.design.theme.accentIndigo
import com.twofasapp.core.design.theme.accentLightBlue
import com.twofasapp.core.design.theme.accentOrange
import com.twofasapp.core.design.theme.accentPink
import com.twofasapp.core.design.theme.accentPurple
import com.twofasapp.core.design.theme.accentTurquoise
import com.twofasapp.core.design.theme.accentYellow
import com.twofasapp.core.design.theme.onSurfaceTertiaryDark
import com.twofasapp.core.design.theme.onSurfaceTertiaryLight
import com.twofasapp.core.design.theme.primaryIndicatorDark
import com.twofasapp.core.design.theme.primaryIndicatorLight
import com.twofasapp.core.design.theme.seed
import com.twofasapp.core.design.theme.serviceBackgroundWithGroupsDark
import com.twofasapp.core.design.theme.serviceBackgroundWithGroupsLight
import com.twofasapp.core.design.theme.successDark
import com.twofasapp.core.design.theme.successLight
import com.twofasapp.core.design.theme.switchThumbDark
import com.twofasapp.core.design.theme.switchThumbLight
import com.twofasapp.core.design.theme.switchTrackDark
import com.twofasapp.core.design.theme.switchTrackLight
import com.twofasapp.core.design.window.ScreenOrientation

val LocalAppTheme = staticCompositionLocalOf { AppTheme.Auto }
val LocalColorTokens = staticCompositionLocalOf { ColorTokens() }
val LocalDynamicColors = staticCompositionLocalOf { true }
val LocalDarkMode = staticCompositionLocalOf { true }

enum class AppTheme {
    Auto, Light, Dark,
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    ScreenOrientation(compactOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val isDynamicColorEnabled = LocalDynamicColors.current

    val isInDarkTheme = when (LocalAppTheme.current) {
        AppTheme.Auto -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

    // Dynamic color requires Android 12 (API 31) — minSdk is 24, so keep the guard.
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme: ColorScheme = when {
        isDynamicColorEnabled && supportsDynamicColor && isInDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        isDynamicColorEnabled && supportsDynamicColor && !isInDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isInDarkTheme -> DarkColors
        else -> LightColors
    }

    val colorTokens = ColorTokens(
        seed = seed,
        primary = colorScheme.primary,
        onPrimary = colorScheme.onPrimary,
        primaryContainer = colorScheme.primaryContainer,
        onPrimaryContainer = colorScheme.onPrimaryContainer,
        secondary = colorScheme.secondary,
        onSecondary = colorScheme.onSecondary,
        secondaryContainer = colorScheme.secondaryContainer,
        onSecondaryContainer = colorScheme.onSecondaryContainer,
        tertiary = colorScheme.tertiary,
        onTertiary = colorScheme.onTertiary,
        tertiaryContainer = colorScheme.tertiaryContainer,
        onTertiaryContainer = colorScheme.onTertiaryContainer,
        error = colorScheme.error,
        onError = colorScheme.onError,
        errorContainer = colorScheme.errorContainer,
        onErrorContainer = colorScheme.onErrorContainer,
        background = colorScheme.background,
        onBackground = colorScheme.onBackground,
        success = if (isInDarkTheme) successDark else successLight,
        notice = colorScheme.error,
        surface = colorScheme.surface,
        onSurface = colorScheme.onSurface,
        surfaceVariant = colorScheme.surfaceVariant,
        onSurfaceVariant = colorScheme.onSurfaceVariant,
        outline = colorScheme.outline,
        outlineVariant = colorScheme.outlineVariant,
        scrim = colorScheme.scrim,
        inverseSurface = colorScheme.inverseSurface,
        inverseOnSurface = colorScheme.inverseOnSurface,
        inversePrimary = colorScheme.inversePrimary,
        surfaceDim = colorScheme.surfaceDim,
        surfaceBright = colorScheme.surfaceBright,
        surfaceContainerLowest = colorScheme.surfaceContainerLowest,
        surfaceContainerLow = colorScheme.surfaceContainerLow,
        surfaceContainer = colorScheme.surfaceContainer,
        surfaceContainerHigh = colorScheme.surfaceContainerHigh,
        surfaceContainerHighest = colorScheme.surfaceContainerHighest,
        bottomBar = colorScheme.background,
        accentLightBlue = accentLightBlue,
        accentIndigo = accentIndigo,
        accentPurple = accentPurple,
        accentTurquoise = accentTurquoise,
        accentGreen = accentGreen,
        accentOrange = accentOrange,
        accentYellow = accentYellow,
        accentPink = accentPink,
        accentBrown = accentBrown,
        // App-specific
        onSurfaceTertiary = if (isInDarkTheme) onSurfaceTertiaryDark else onSurfaceTertiaryLight,
        primaryIndicator = if (isInDarkTheme) primaryIndicatorDark else primaryIndicatorLight,
        serviceBackgroundWithGroups = if (isInDarkTheme) serviceBackgroundWithGroupsDark else serviceBackgroundWithGroupsLight,
        switchTrack = if (isInDarkTheme) switchTrackDark else switchTrackLight,
        switchThumb = if (isInDarkTheme) switchThumbDark else switchThumbLight,
    )

    CompositionLocalProvider(
        LocalColorTokens provides colorTokens,
        LocalDarkMode provides isInDarkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}