package com.twofasapp.designsystem.internal

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@Stable
class ThemeColorsDark : ThemeColors() {
    override val primary: Color = Color(0xFFF83A40)
    override val background: Color = Color(0xFF101116)
    override val surface: Color = Color(0xFF1A1B21)
    override val surfaceVariant: Color = Color(0xFF232323)
    override val onSurfacePrimary: Color = Color(0xFFFFFFFF)
    override val onSurfaceSecondary: Color = Color(0xFF636363)
    override val onSurfaceTertiary: Color = Color(0xFF9E9E9E)
    override val primaryIndicator: Color = Color(0xFF482227)
    override val serviceBackgroundWithGroups: Color = Color(0xFF17181B)
    override val switchTrack: Color = Color(0xFF1F2025)
    override val switchThumb: Color = Color(0xFF48494E)
}