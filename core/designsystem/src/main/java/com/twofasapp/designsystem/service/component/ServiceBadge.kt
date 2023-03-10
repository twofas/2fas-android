package com.twofasapp.designsystem.service.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
internal fun ServiceBadge(color: Color) {
    Box(
        Modifier
            .fillMaxHeight()
            .width(5.dp)
            .background(color)
    )
}

@Preview
@Composable
private fun Preview() {
    ServiceBadge(color = TwTheme.color.primary)
}