package com.twofasapp.core.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import com.twofasapp.core.design.theme.ThemeColors
import com.twofasapp.core.design.theme.ThemeDimens
import com.twofasapp.core.design.theme.ThemeShapes
import com.twofasapp.core.design.theme.ThemeTypo

object MdtTheme {
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