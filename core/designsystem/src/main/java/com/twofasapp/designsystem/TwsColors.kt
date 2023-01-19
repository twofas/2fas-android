package com.twofasapp.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

interface TwsColors {
    val primary: Color
    val background: Color
    val surface: Color
    val onSurface: Color
    val onSurfaceDarker: Color
    val buttonPrimary: Color
    val onButtonPrimary: Color
    val divider: Color
}


val LocalTwsColors = staticCompositionLocalOf<TwsColors> {
    TwsColorsLight()
}