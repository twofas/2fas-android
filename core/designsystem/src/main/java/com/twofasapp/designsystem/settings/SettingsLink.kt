package com.twofasapp.designsystem.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIcon
import com.twofasapp.locale.TwLocale

@Composable
fun SettingsLink(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: Painter? = null,
    image: Painter? = null,
    textColor: Color = TwTheme.color.onSurfacePrimary,
    textColorSecondary: Color = TwTheme.color.onSurfaceSecondary,
    endContent: (@Composable () -> Unit)? = null,
    showEmptySpaceWhenNoIcon: Boolean = true,
    external: Boolean = false,
    alignCenterIcon: Boolean = true,
    onClick: (() -> Unit)? = null
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) { onClick?.invoke() }
            .heightIn(min = 56.dp)
            .padding(vertical = 16.dp)
            .padding(start = 24.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(icon, image, showEmptySpaceWhenNoIcon)

            Spacer(Modifier.size(24.dp))

            Column(Modifier.weight(1f)) {
                Title(
                    title = title,
                    textColor = textColor,
                    modifier = Modifier.fillMaxWidth()
                )

                if (alignCenterIcon) {
                    Subtitle(
                        subtitle = subtitle,
                        textColorSecondary = textColorSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (endContent != null) {
                Spacer(Modifier.width(8.dp))
                endContent.invoke()
            } else if (external) {
                TwIcon(
                    painter = TwIcons.ExternalLink,
                    tint = TwTheme.color.iconTint,
                    modifier = Modifier.size(20.dp).alpha(0.7f)
                )
            }
        }

        if (alignCenterIcon.not()) {
            Subtitle(
                subtitle = subtitle,
                textColorSecondary = textColorSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp)
            )
        }
    }
}

@Composable
private fun Image(
    icon: Painter?,
    image: Painter?,
    showEmptySpaceWhenIconMissing: Boolean,
) {
    if (icon != null) {
        Icon(painter = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = TwTheme.color.primary)
    } else if (image != null)
        Image(painter = image, contentDescription = null, modifier = Modifier.size(24.dp))
    else if (showEmptySpaceWhenIconMissing) {
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun Title(
    title: String,
    textColor: Color,
    modifier: Modifier,
) {
    Text(
        text = title,
        style = TwTheme.typo.body1,
        color = textColor,
        modifier = modifier
    )
}

@Composable
private fun Subtitle(
    subtitle: String?,
    textColorSecondary: Color,
    modifier: Modifier,
) {
    if (subtitle != null) {
        Text(
            text = subtitle,
            style = TwTheme.typo.body3,
            color = textColorSecondary,
            modifier = modifier.padding(top = 2.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Column {

        SettingsLink(
            title = TwLocale.strings.placeholder,
            icon = TwIcons.Placeholder,
            endContent = {
                Text(text = "Test")
            }
        )

        SettingsLink(
            title = TwLocale.strings.placeholder,
            icon = TwIcons.Placeholder,
            external = true
        )
    }
}

@Preview
@Composable
private fun PreviewWithSubtitle() {
    SettingsLink(
        title = TwLocale.strings.placeholder,
        subtitle = TwLocale.strings.placeholderMedium,
        icon = TwIcons.Placeholder,
    )
}

@Preview
@Composable
private fun PreviewWithSubtitleIconNotAligned() {
    SettingsLink(
        title = TwLocale.strings.placeholder,
        subtitle = TwLocale.strings.placeholderMedium,
        icon = TwIcons.Placeholder,
        alignCenterIcon = false,
    )
}