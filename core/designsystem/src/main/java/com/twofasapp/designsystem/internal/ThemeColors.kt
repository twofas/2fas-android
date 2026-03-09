package com.twofasapp.designsystem.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.twofasapp.designsystem.TwTheme

data class ThemeColors(
    val seed: Color = Color.Unspecified,
    val primary: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
    val surface: Color = Color.Unspecified,
    val surfaceVariant: Color = Color.Unspecified,
    val onSurfacePrimary: Color = Color.Unspecified,
    val onSurfaceSecondary: Color = Color.Unspecified,
    val onSurfaceTertiary: Color = Color.Unspecified,
    val primaryIndicator: Color = Color.Unspecified,
    val serviceBackgroundWithGroups: Color = Color.Unspecified,
    val switchTrack: Color = Color.Unspecified,
    val switchThumb: Color = Color.Unspecified,
) {
    val divider: Color
        get() = surfaceVariant

    val iconTint: Color
        get() = onSurfaceSecondary

    val error: Color = Color(0xFFF83A40)
    val accentLightBlue: Color = Color(0xFF7F9CFF)
    val accentIndigo: Color = Color(0xFF5E5CE6)
    val accentPurple: Color = Color(0xFF8C49DE)
    val accentTurquoise: Color = Color(0xFF2FCFBC)
    val accentGreen: Color = Color(0xFF03BF38)
    val accentRed: Color
        get() = primary
    val accentOrange: Color = Color(0xFFFF7A00)
    val accentYellow: Color = Color(0xFFFFBA0A)
    val accentPink: Color = Color(0xFFca49de)
    val accentBrown: Color = Color(0xFFbd8857)
}

@Composable
fun isDarkTheme() = TwTheme.color.background.luminance() < 0.5
