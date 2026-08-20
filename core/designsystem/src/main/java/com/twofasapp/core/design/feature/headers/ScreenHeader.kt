/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.feature.headers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewAllThemesInColumn
import com.twofasapp.core.design.foundation.preview.PreviewTextMedium

@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    image: Painter? = null,
    icon: Painter? = null,
    iconTint: Color = MdtTheme.color.primary,
    iconContent: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        image?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
            )

            Space(16.dp)
        }

        icon?.let {
            Icon(
                painter = it,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(70.dp)
                    .padding(8.dp),
            )

            Space(16.dp)
        }

        iconContent?.let {
            it.invoke(this)
            Space(16.dp)
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MdtTheme.typo.xl4.medium,
            color = MdtTheme.color.onSurface,
            textAlign = TextAlign.Center,
        )

        description?.let {
            Space(12.dp)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = MdtTheme.typo.sm.normal,
                color = MdtTheme.color.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewAllThemesInColumn {
        ScreenHeader(
            title = "Headline",
            description = PreviewTextMedium,
            image = MdtIcons.Placeholder,
        )
    }
}