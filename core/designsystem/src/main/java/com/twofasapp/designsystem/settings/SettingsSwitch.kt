package com.twofasapp.designsystem.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.locale.TwLocale

@Composable
fun SettingsSwitch(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    subtitle: String? = null,
    icon: Painter? = null,
    image: Painter? = null,
    textColor: Color = TwTheme.color.onSurfacePrimary,
    textColorSecondary: Color = TwTheme.color.onSurfaceSecondary,
    showEmptySpaceWhenIconMissing: Boolean = true,
    alignCenterIcon: Boolean = true,
) {
    SettingsLink(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        image = image,
        textColor = textColor,
        textColorSecondary = textColorSecondary,
        endContent = {
            TwSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
        showEmptySpaceWhenNoIcon = showEmptySpaceWhenIconMissing,
        alignCenterIcon = alignCenterIcon,
        onClick = { onCheckedChange?.invoke((checked.not())) }
    )
}


@Preview
@Composable
private fun Preview() {
    SettingsSwitch(
        title = TwLocale.strings.placeholder,
        checked = false,
        icon = TwIcons.Placeholder,
    )
}
