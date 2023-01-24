package com.twofasapp.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import com.twofasapp.designsystem.internal.LocalThemeColors
import com.twofasapp.designsystem.internal.ThemeColors
import com.twofasapp.designsystem.internal.ThemeDimens
import com.twofasapp.designsystem.internal.ThemeShapes
import com.twofasapp.designsystem.internal.ThemeTypo

object TwTheme {
    val color: ThemeColors
        @Composable
        get() = LocalThemeColors.current

    val typo: ThemeTypo
        @Composable
        get() = ThemeTypo()

    val shape: ThemeShapes
        @Composable
        get() = ThemeShapes()

    val dimen: ThemeDimens
        @Composable
        get() = ThemeDimens()

    val isDark: Boolean
        @Composable
        get() = color.background.luminance() < 0.5
}