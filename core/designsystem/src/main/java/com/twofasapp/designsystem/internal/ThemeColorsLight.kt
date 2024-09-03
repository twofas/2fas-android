package com.twofasapp.designsystem.internal

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@Stable
class ThemeColorsLight : ThemeColors() {
    override val primary: Color = Color(0xFFED1C24)
    override val background: Color = Color(0xFFFFFFFF)
    override val surface: Color = Color(0xFFF9F9F9)
    override val surfaceVariant: Color = Color(0xFFEEEEEE)
    override val onSurfacePrimary: Color = Color(0xFF000000)
    override val onSurfaceSecondary: Color = Color(0xFF9E9E9E)
    override val onSurfaceTertiary: Color = Color(0xFF4C4C4C)
    override val primaryIndicator: Color = Color(0xFFF8E2E3)
    override val serviceBackgroundWithGroups: Color = Color(0xFFFCFCFC)
    override val switchTrack: Color = Color(0xFFEEEEEE)
    override val switchThumb: Color = Color(0xFFBBBBBB)
}