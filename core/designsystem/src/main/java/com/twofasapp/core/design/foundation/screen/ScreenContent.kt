/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator

@Composable
fun ScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ScreenEmpty(
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter = MdtIcons.Info,
) {
    ScreenMessage(
        text = text,
        icon = icon,
        modifier = modifier,
    )
}

@Composable
fun ScreenError(
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter = MdtIcons.Warning,
) {
    ScreenMessage(
        text = text,
        icon = icon,
        modifier = modifier,
    )
}

@Composable
private fun ScreenMessage(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MdtTheme.color.onSurfaceVariant,
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = text,
            style = MdtTheme.typo.base.normal,
            color = MdtTheme.color.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}