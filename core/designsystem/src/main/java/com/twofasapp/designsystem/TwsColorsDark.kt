package com.twofasapp.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TwsColorsDark(
    override val primary: Color = Color(0xFFED1C24),
    override val background: Color = Color(0xFF15161B),
    override val surface: Color = Color(0xFF15161C),
    override val onSurface: Color = Color(0xFFFFFFFF),
    override val onSurfaceDarker: Color = Color(0xFF555555),
    override val buttonPrimary: Color = primary,
    override val onButtonPrimary: Color = Color(0xFFFFFFFF),
    override val divider: Color = Color(0x0FFFFFFF),
) : TwsColors