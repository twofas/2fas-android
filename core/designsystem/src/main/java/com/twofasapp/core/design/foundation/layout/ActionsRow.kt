/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.preview.PreviewTheme

@Composable
fun ActionsRow(
    modifier: Modifier = Modifier,
    useHorizontalPadding: Boolean = false,
    spacing: Dp = 4.dp,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = if (useHorizontalPadding) 4.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

@Preview
@Composable
private fun Previews() {
    PreviewTheme {
        ActionsRow {
            IconButton(icon = MdtIcons.Placeholder)
            IconButton(icon = MdtIcons.Placeholder)
            IconButton(icon = MdtIcons.Placeholder)
        }
    }
}