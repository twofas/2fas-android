package com.twofasapp.core.design.feature.settings

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
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.locale.MdtLocale

enum class SubtitleGravity { Bottom, End }

@Composable
fun SettingsLink(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: Painter? = null,
    image: Painter? = null,
    iconTint: Color? = null,
    textColor: Color = MdtTheme.color.onSurface,
    textColorSecondary: Color = MdtTheme.color.onSurfaceVariant,
    endContent: (@Composable () -> Unit)? = null,
    showEmptySpaceWhenNoIcon: Boolean = true,
    external: Boolean = false,
    enabled: Boolean = true,
    subtitleGravity: SubtitleGravity = SubtitleGravity.Bottom,
    alignCenterIcon: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null && enabled) { onClick?.invoke() }
            .heightIn(min = 56.dp)
            .padding(vertical = if (endContent == null) 16.dp else 8.dp)
            .padding(start = 24.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                icon,
                image,
                iconTint,
                showEmptySpaceWhenNoIcon,
                modifier = Modifier.alpha(if (enabled) 1f else 0.3f),
            )

            Spacer(Modifier.size(24.dp))

            Column(Modifier.weight(1f)) {
                Title(
                    title = title,
                    textColor = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (enabled) 1f else 0.4f),
                )

                if (alignCenterIcon && subtitleGravity == SubtitleGravity.Bottom) {
                    Subtitle(
                        subtitle = subtitle,
                        textColorSecondary = textColorSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (enabled) 1f else 0.9f),
                    )
                }
            }

            if (endContent != null) {
                Spacer(Modifier.width(8.dp))
                endContent.invoke()
            } else if (external) {
                Icon(
                    painter = MdtIcons.ExternalLink,
                    tint = MdtTheme.color.iconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(0.7f),
                )
            } else if (subtitleGravity == SubtitleGravity.End) {
                Subtitle(
                    subtitle = subtitle,
                    textColorSecondary = textColorSecondary,
                    modifier = Modifier,
                )
            }
        }

        if (alignCenterIcon.not() && subtitleGravity == SubtitleGravity.Bottom) {
            Subtitle(
                subtitle = subtitle,
                textColorSecondary = textColorSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp),
            )
        }
    }
}

@Composable
private fun Image(
    icon: Painter?,
    image: Painter?,
    iconTint: Color?,
    showEmptySpaceWhenIconMissing: Boolean,
    modifier: Modifier = Modifier,
) {
    if (icon != null) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = modifier.size(24.dp),
            tint = if (iconTint != null) iconTint else MdtTheme.color.primary,
        )
    } else if (image != null) {
        Image(painter = image, contentDescription = null, modifier = Modifier.size(24.dp))
    } else if (showEmptySpaceWhenIconMissing) {
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
        style = MdtTheme.typo.base.normal,
        color = textColor,
        modifier = modifier,
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
            style = MdtTheme.typo.sm.normal,
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
            title = MdtLocale.strings.placeholder,
            icon = MdtIcons.Placeholder,
            endContent = {
                Text(text = "Test")
            },
        )

        SettingsLink(
            title = MdtLocale.strings.placeholder,
            icon = MdtIcons.Placeholder,
            external = true,
        )
    }
}

@Preview
@Composable
private fun PreviewWithSubtitle() {
    SettingsLink(
        title = MdtLocale.strings.placeholder,
        subtitle = MdtLocale.strings.placeholderMedium,
        icon = MdtIcons.Placeholder,
    )
}

@Preview
@Composable
private fun PreviewWithSubtitleEnd() {
    SettingsLink(
        title = MdtLocale.strings.placeholder,
        subtitle = MdtLocale.strings.placeholder,
        icon = MdtIcons.Placeholder,
        subtitleGravity = SubtitleGravity.End,
    )
}

@Preview
@Composable
private fun PreviewWithSubtitleIconNotAligned() {
    SettingsLink(
        title = MdtLocale.strings.placeholder,
        subtitle = MdtLocale.strings.placeholderMedium,
        icon = MdtIcons.Placeholder,
        alignCenterIcon = false,
    )
}