package com.twofasapp.core.design.foundation.other

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.twofasapp.core.design.MdtTheme

@Composable
fun Divider(
    modifier: Modifier = Modifier.fillMaxWidth(),
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = MdtTheme.color.divider,
) {
    Divider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}

@Preview
@Composable
private fun Preview() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
    )
}