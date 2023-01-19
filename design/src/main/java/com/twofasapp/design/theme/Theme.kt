package com.twofasapp.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.twofasapp.prefs.model.AppTheme

@Composable
fun AppThemeLegacy(
    content: @Composable () -> Unit,
) {
    val isNight = when (ThemeState.appTheme) {
        AppTheme.AUTO -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    MaterialTheme(
        colors = if (isNight) darkColors(
            primary = primary,
            background = Color(0xFF16161B),
            surface = Color(0xFF16161B),
        ) else lightColors(
            primary = primary,
            background = Color.White,
            surface = Color.White,
        )
    ) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
            color = if (isNight) statusbarDark else statusbarLight,
            darkIcons = isNight.not(),
        )

        content()
    }
}

@Composable
fun isNight() = MaterialTheme.colors.background.luminance() < 0.5