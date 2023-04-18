package com.twofasapp.designsystem.common

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.twofasapp.designsystem.TwTheme

@Composable
fun TwIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = TwTheme.color.iconTint
) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}