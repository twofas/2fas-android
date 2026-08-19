/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.topbar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.twofasapp.core.design.MdtTheme

@Composable
fun TopAppBarTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MdtTheme.color.onBackground,
    style: TextStyle = MdtTheme.typo.medium.xl,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}