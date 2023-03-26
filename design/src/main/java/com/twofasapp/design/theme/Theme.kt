package com.twofasapp.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import com.twofasapp.designsystem.TwTheme

@Composable
fun AppThemeLegacy(
    content: @Composable () -> Unit,
) {
//    val isNight = when (ThemeState.appTheme) {
//        AppTheme.AUTO -> isSystemInDarkTheme()
//        AppTheme.LIGHT -> false
//        AppTheme.DARK -> true
//    }

//    MaterialTheme(
//        colors = if (isNight) darkColors(
//            primary = primary,
//            background = Color(0xFF16161B),
//            surface = Color(0xFF16161B),
//        ) else lightColors(
//            primary = primary,
//            background = Color.White,
//            surface = Color.White,
//        )
//    ) {
//        val systemUiController = rememberSystemUiController()
//        systemUiController.setSystemBarsColor(
//            color = if (isNight) statusbarDark else statusbarLight,
//            darkIcons = isNight.not(),
//        )
//
//        content()
//    }
}

@Composable
fun isNight() = TwTheme.color.background.luminance() < 0.5