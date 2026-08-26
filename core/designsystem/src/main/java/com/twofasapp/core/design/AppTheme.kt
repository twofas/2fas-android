package com.twofasapp.core.design

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.window.core.layout.WindowWidthSizeClass
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.theme.OverriddenDarkColors
import com.twofasapp.core.design.theme.OverriddenLightColors
import com.twofasapp.core.design.theme.ThemeColors
import com.twofasapp.core.design.theme.onSurfacePrimaryDark
import com.twofasapp.core.design.theme.onSurfacePrimaryLight
import com.twofasapp.core.design.theme.onSurfaceSecondaryDark
import com.twofasapp.core.design.theme.onSurfaceSecondaryLight
import com.twofasapp.core.design.theme.onSurfaceTertiaryDark
import com.twofasapp.core.design.theme.onSurfaceTertiaryLight
import com.twofasapp.core.design.theme.primaryIndicatorDark
import com.twofasapp.core.design.theme.primaryIndicatorLight
import com.twofasapp.core.design.theme.seedDark
import com.twofasapp.core.design.theme.seedLight
import com.twofasapp.core.design.theme.serviceBackgroundWithGroupsDark
import com.twofasapp.core.design.theme.serviceBackgroundWithGroupsLight
import com.twofasapp.core.design.theme.switchThumbDark
import com.twofasapp.core.design.theme.switchThumbLight
import com.twofasapp.core.design.theme.switchTrackDark
import com.twofasapp.core.design.theme.switchTrackLight

val LocalAppTheme = staticCompositionLocalOf { AppTheme.Auto }
val LocalThemeColors = staticCompositionLocalOf { ThemeColors() }
val LocalDynamicColors = staticCompositionLocalOf { false }

enum class AppTheme {
    Auto, Light, Dark,
}

@Composable
fun MainAppTheme(
    content: @Composable () -> Unit,
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val isDynamicColorEnabled = LocalDynamicColors.current

    val isInDarkTheme = when (LocalAppTheme.current) {
        AppTheme.Auto -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

    val colorScheme: ColorScheme = when {
        isDynamicColorEnabled && isInDarkTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicDarkColorScheme(LocalContext.current)
        isDynamicColorEnabled && !isInDarkTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(LocalContext.current)
        isInDarkTheme -> OverriddenDarkColors
        else -> OverriddenLightColors
    }

    val themeColors = ThemeColors(
        seed = if (isInDarkTheme) seedDark else seedLight,
        primary = colorScheme.primary,
        background = colorScheme.background,
        surface = colorScheme.surface,
        surfaceVariant = colorScheme.surfaceVariant,
        onSurfacePrimary = if (isInDarkTheme) onSurfacePrimaryDark else onSurfacePrimaryLight,
        onSurfaceSecondary = if (isInDarkTheme) onSurfaceSecondaryDark else onSurfaceSecondaryLight,
        onSurfaceTertiary = if (isInDarkTheme) onSurfaceTertiaryDark else onSurfaceTertiaryLight,
        primaryIndicator = if (isInDarkTheme) primaryIndicatorDark else primaryIndicatorLight,
        serviceBackgroundWithGroups = if (isInDarkTheme) serviceBackgroundWithGroupsDark else serviceBackgroundWithGroupsLight,
        switchTrack = if (isInDarkTheme) switchTrackDark else switchTrackLight,
        switchThumb = if (isInDarkTheme) switchThumbDark else switchThumbLight,
    )

    CompositionLocalProvider(
        LocalThemeColors provides themeColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val activity = LocalContext.currentActivity
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isPhone = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    if (isPhone) {
        DisposableEffect(Unit) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                activity.requestedOrientation = originalOrientation
            }
        }
    }
}