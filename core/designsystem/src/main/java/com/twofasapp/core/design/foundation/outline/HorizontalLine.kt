package com.twofasapp.core.design.foundation.outline

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme

@Composable
fun HorizontalLine(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.5.dp,
    color: Color = MdtTheme.color.divider,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}