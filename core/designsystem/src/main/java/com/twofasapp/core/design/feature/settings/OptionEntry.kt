/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewColumn
import com.twofasapp.core.design.foundation.preview.PreviewText
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTextMedium

val OptionEntryPaddingHorizontal = 16.dp
val OptionEntryPadding = PaddingValues(
    start = OptionEntryPaddingHorizontal,
    top = 14.dp,
    end = OptionEntryPaddingHorizontal,
    bottom = 14.dp,
)

@Composable
fun OptionEntry(
    title: String?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = OptionEntryPadding,
    subtitle: String? = null,
    subtitleAnnotated: AnnotatedString? = null,
    value: String? = null,
    icon: Painter? = null,
    externalIcon: Painter = MdtIcons.ExternalLink,
    image: Painter? = null,
    iconTint: Color? = null,
    titleColor: Color = MdtTheme.color.onSurface,
    subtitleColor: Color = MdtTheme.color.onSurfaceVariant,
    external: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = if (external.not()) {
        null
    } else {
        {
            Icon(
                painter = externalIcon,
                contentDescription = null,
                tint = MdtTheme.color.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    },
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(modifier)
            .alpha(if (enabled) 1f else 0.4f)
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled) { onClick.invoke() }
                } else {
                    Modifier
                },
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint ?: MdtTheme.color.primary,
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        if (image != null) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(
            modifier = if (value != null) {
                Modifier.weight(0.75f)
            } else {
                Modifier.weight(1f)
            },
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MdtTheme.typo.medium.base,
                    color = titleColor,
                )
            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MdtTheme.typo.regular.sm,
                    color = subtitleColor,
                )
            } else if (subtitleAnnotated != null) {
                Text(
                    text = subtitleAnnotated,
                    style = MdtTheme.typo.regular.sm,
                    color = subtitleColor,
                )
            }
        }

        if (value != null) {
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = value,
                style = MdtTheme.typo.regular.sm,
                color = titleColor,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.25f),
            )
        }

        if (content != null) {
            Spacer(modifier = Modifier.width(8.dp))
            content.invoke()
        }
    }
}

@Preview
@Composable
private fun Previews() {
    PreviewColumn {
        OptionEntry(
            title = PreviewText,
            icon = MdtIcons.Placeholder,
        )
        OptionEntry(
            title = PreviewText,
            subtitle = PreviewTextMedium,
            icon = MdtIcons.Placeholder,
            external = true,
        )

        OptionEntry(
            title = PreviewText,
            subtitle = PreviewTextLong,
            icon = MdtIcons.Placeholder,
        )

        OptionEntry(
            title = PreviewText,
            subtitle = PreviewTextLong,
            value = PreviewText,
            icon = MdtIcons.Placeholder,
        )

        OptionEntry(
            title = PreviewText,
            subtitle = PreviewTextLong,
            icon = MdtIcons.Placeholder,
            enabled = false,
        )
    }
}