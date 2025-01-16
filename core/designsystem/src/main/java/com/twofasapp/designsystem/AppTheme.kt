package com.twofasapp.designsystem

import android.content.pm.ActivityInfo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.designsystem.internal.LocalThemeColors
import com.twofasapp.designsystem.internal.ThemeColors
import com.twofasapp.designsystem.internal.ThemeColorsDark
import com.twofasapp.designsystem.internal.ThemeColorsLight
import com.twofasapp.designsystem.ktx.currentActivity

val LocalAppTheme = staticCompositionLocalOf { AppTheme.Auto }

enum class AppTheme {
    Auto, Light, Dark,
}

@Composable
fun MainAppTheme(
    content: @Composable () -> Unit
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val isInDarkTheme = when (LocalAppTheme.current) {
        AppTheme.Auto -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

    val colors: ThemeColors = when (isInDarkTheme) {
        true -> ThemeColorsDark()
        false -> ThemeColorsLight()
    }

    val colorScheme: ColorScheme = when (isInDarkTheme) {
        true -> darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onSurfacePrimary,
            background = colors.background,
            onBackground = colors.onSurfacePrimary,
            surface = colors.surface,
            onSurface = colors.onSurfacePrimary,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.onSurfacePrimary,
        )

        else -> lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onSurfacePrimary,
            background = colors.background,
            onBackground = colors.onSurfacePrimary,
            surface = colors.surface,
            onSurface = colors.onSurfacePrimary,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.onSurfacePrimary,
        )
    }

    CompositionLocalProvider(
        LocalThemeColors provides colors,
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

    DisposableEffect(Unit) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}
