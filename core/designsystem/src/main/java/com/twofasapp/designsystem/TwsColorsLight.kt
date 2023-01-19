package com.twofasapp.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TwsColorsLight(
    override val primary: Color = Color(0xFFED1C24),
    override val background: Color = Color(0xFFFFFFFF),
    override val surface: Color = Color(0xFFFFFFFE),
    override val onSurface: Color = Color(0xDD000000),
    override val onSurfaceDarker: Color = Color(0xFF9E9E9E),
    override val buttonPrimary: Color = primary,
    override val onButtonPrimary: Color = Color(0xFFFFFFFF),
    override val divider: Color = Color(0x14000000),
) : TwsColors