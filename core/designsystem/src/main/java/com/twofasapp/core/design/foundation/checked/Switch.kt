package com.twofasapp.core.design.foundation.checked

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewAllThemesInRow

@Composable
fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = {},
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = {
            if (checked) {
                Icon(
                    painter = MdtIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

object SwitchDefaults {

    @Composable
    fun colors(
        checkedIconColor: Color = MdtTheme.color.primary,
    ): SwitchColors {
        return androidx.compose.material3.SwitchDefaults.colors(
            checkedIconColor = checkedIconColor,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewAllThemesInRow {
        Switch(checked = false, enabled = false)
        Switch(checked = true, enabled = false)

        Switch(checked = false)
        Switch(checked = true)
    }
}