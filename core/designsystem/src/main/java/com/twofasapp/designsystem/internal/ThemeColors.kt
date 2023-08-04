package com.twofasapp.designsystem.internal

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

abstract class ThemeColors {
    abstract val background: Color
    abstract val surface: Color
    abstract val surfaceVariant: Color
    abstract val onSurfacePrimary: Color
    abstract val onSurfaceSecondary: Color
    abstract val onSurfaceTertiary: Color
    abstract val primaryIndicator: Color
    abstract val serviceBackgroundWithGroups: Color
    abstract val switchTrack: Color
    abstract val switchThumb: Color

    val primary: Color = Color(0xFFED1C24)
    val primaryDark: Color = Color(0xFFD81F26)

    val button: Color = primary
    val onButton: Color = Color.White

    val divider: Color
        get() = surfaceVariant

    val iconTint: Color
        get() = onSurfaceSecondary

    val error: Color = Color(0xFFB9171E)

    val accentLightBlue: Color = Color(0xFF7F9CFF)
    val accentIndigo: Color = Color(0xFF5E5CE6)
    val accentPurple: Color = Color(0xFF8C49DE)
    val accentTurquoise: Color = Color(0xFF2FCFBC)
    val accentGreen: Color = Color(0xFF03BF38)
    val accentRed: Color = Color(0xFFED1C24)
    val accentOrange: Color = Color(0xFFFF7A00)
    val accentYellow: Color = Color(0xFFFFBA0A)
    val accentPink: Color = Color(0xFFca49de)
    val accentBrown: Color = Color(0xFFbd8857)
}

val LocalThemeColors = staticCompositionLocalOf<ThemeColors> {
    ThemeColorsLight()
}
