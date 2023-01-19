package com.twofasapp.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MainAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (view.isInEditMode.not()) {
        SideEffect {
            with((view.context as Activity).window) {
                statusBarColor = Color.Transparent.toArgb()
                navigationBarColor = Color.Transparent.toArgb()

                WindowCompat.setDecorFitsSystemWindows(this, false)
            }
        }
    }

    val colors: TwsColors = when (isSystemInDarkTheme()) {
        true -> TwsColorsDark()
        false -> TwsColorsLight()
    }

    val colorScheme: ColorScheme = when (isSystemInDarkTheme()) {
        true -> darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onSurface,
            background = colors.background,
            onBackground = colors.onSurface,
            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.onSurface,
        )

        false -> lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onSurface,
            background = colors.background,
            onBackground = colors.onSurface,
            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surface,
            onSurfaceVariant = colors.onSurface,
        )
    }

    CompositionLocalProvider(
        LocalTwsColors provides colors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}