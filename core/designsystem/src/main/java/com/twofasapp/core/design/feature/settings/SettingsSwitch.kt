package com.twofasapp.core.design.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.checked.Switch
import com.twofasapp.locale.TwLocale

@Composable
fun SettingsSwitch(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    subtitle: String? = null,
    icon: Painter? = null,
    image: Painter? = null,
    textColor: Color = MdtTheme.color.onSurfacePrimary,
    textColorSecondary: Color = MdtTheme.color.onSurfaceSecondary,
    showEmptySpaceWhenIconMissing: Boolean = true,
    alignCenterIcon: Boolean = true,
) {
    SettingsLink(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        image = image,
        enabled = enabled,
        textColor = textColor,
        textColorSecondary = textColorSecondary,
        endContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.alpha(if (enabled) 1f else 0.4f),
            )
        },
        showEmptySpaceWhenNoIcon = showEmptySpaceWhenIconMissing,
        alignCenterIcon = alignCenterIcon,
        onClick = { onCheckedChange?.invoke((checked.not())) },
    )
}

@Preview
@Composable
private fun Preview() {
    SettingsSwitch(
        title = TwLocale.strings.placeholder,
        checked = false,
        icon = MdtIcons.Placeholder,
    )
}