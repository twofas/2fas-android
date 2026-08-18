package com.twofasapp.core.design.foundation.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.twofasapp.core.design.MdtTheme

@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = MdtTheme.color.iconTint,
) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}