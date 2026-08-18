package com.twofasapp.core.design.foundation.checked

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.MdtTheme

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = MdtTheme.color.primary,
        checkedBorderColor = Color.Transparent,
        checkedIconColor = MdtTheme.color.primary,

        uncheckedThumbColor = MdtTheme.color.switchThumb,
        uncheckedTrackColor = MdtTheme.color.switchTrack,
        uncheckedBorderColor = MdtTheme.color.switchThumb.copy(alpha = 0.3f),
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
        interactionSource = interactionSource,
    )
}

@Preview
@Composable
private fun Checked() {
    Switch(checked = true, onCheckedChange = null)
}

@Preview
@Composable
private fun Unchecked() {
    Switch(checked = false, onCheckedChange = null)
}