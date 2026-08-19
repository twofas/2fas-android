package com.twofasapp.core.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.luminance
import com.twofasapp.core.design.theme.ColorTokens
import com.twofasapp.core.design.theme.TypographyTokens

object MdtTheme {
    val color: ColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalColorTokens.current

    val typo: TypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = TypographyTokens(color)

    val isDark: Boolean
        @Composable
        get() = color.background.luminance() < 0.5
}