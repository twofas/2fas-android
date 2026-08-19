/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    icon: Painter,
    iconTint: Color = MdtTheme.color.onSurfaceVariant,
    iconSize: Dp = 24.dp,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    containerColor: Color = Color.Transparent,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    content: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor, shape)
            .clickable(enabled) { onClick() },
    ) {
        if (content != null) {
            content()
        } else {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = if (enabled) iconTint else iconTint.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(contentPadding)
                    .size(iconSize),
            )
        }
    }
}

@Preview
@Composable
private fun Previews() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(
            icon = MdtIcons.Placeholder,
            iconTint = Color.White,
            modifier = Modifier,
            containerColor = Color.DarkGray,
        )

        IconButton(
            icon = MdtIcons.Placeholder,
            iconTint = Color.White,
            modifier = Modifier.size(96.dp),
            containerColor = Color.DarkGray,
        )

        IconButton(
            icon = MdtIcons.Placeholder,
            iconTint = Color.White,
            iconSize = 48.dp,
            modifier = Modifier.size(96.dp),
            containerColor = Color.DarkGray,
            shape = RoundedCornerShape(12.dp),
        )
    }
}