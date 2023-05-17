package com.twofasapp.designsystem

import android.app.Activity
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.twofasapp.designsystem.internal.LocalThemeColors
import com.twofasapp.designsystem.internal.ThemeColors
import com.twofasapp.designsystem.internal.ThemeColorsDark
import com.twofasapp.designsystem.internal.ThemeColorsLight

val LocalAppTheme = staticCompositionLocalOf { AppTheme.Auto }

enum class AppTheme {
    Auto, Light, Dark,
}

@Composable
fun isGestureNavigationEnabled(view: View): Boolean {
    val windowInsets = WindowInsetsCompat.toWindowInsetsCompat(
        view.rootWindowInsets, view
    )
    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())

    return insets.left > 0 && insets.right > 0
}

@Composable
fun MainAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val systemUiController = rememberSystemUiController()

    if (view.isInEditMode.not()) {
        SideEffect {
            with((view.context as Activity).window) {
                statusBarColor = Color.Transparent.toArgb()
                navigationBarColor = Color.Transparent.toArgb()

                WindowCompat.setDecorFitsSystemWindows(this, false)

//                if (BuildConfig.BUILD_TYPE.equals("release", true)) {
//                    setFlags(
//                        WindowManager.LayoutParams.FLAG_SECURE,
//                        WindowManager.LayoutParams.FLAG_SECURE
//                    )
//                }
            }
        }
    }

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

    SideEffect {
        systemUiController.setStatusBarColor(color = colors.background)
    }

    if (!isGestureNavigationEnabled(view)) {
        SideEffect {
            systemUiController.setNavigationBarColor(color = colors.background)
        }
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
