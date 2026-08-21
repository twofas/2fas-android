/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.checked

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewColumn

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    checked: Boolean,
    color: Color = MdtTheme.color.primary,
    size: Dp = 24.dp,
) {
    Icon(
        painter = if (checked) MdtIcons.CircleCheckFilled else MdtIcons.CircleUncheck,
        contentDescription = null,
        modifier = modifier.then(Modifier.size(size)),
        tint = if (checked) color else MdtTheme.color.surfaceContainerHighest,
    )
}

@Preview
@Composable
private fun Previews() {
    PreviewColumn {
        CheckIcon(checked = false)
        CheckIcon(checked = true)
    }
}