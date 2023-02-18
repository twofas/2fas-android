package com.twofasapp.designsystem.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun TwSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = TwTheme.color.primary,
        checkedBorderColor = Color.Transparent,
        checkedIconColor = TwTheme.color.primary,

        uncheckedThumbColor = TwTheme.color.onSurfaceTertiary,
        uncheckedTrackColor = TwTheme.color.onSurfaceSecondary,
        uncheckedBorderColor = TwTheme.color.onSurfaceTertiary,
        uncheckedIconColor = TwTheme.color.onSurfacePrimary,
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

@Preview
@Composable
private fun Checked() {
    TwSwitch(checked = true, onCheckedChange = null)
}

@Preview
@Composable
private fun Unchecked() {
    TwSwitch(checked = false, onCheckedChange = null)
}