/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.LocalAppTheme
import com.twofasapp.core.design.LocalDynamicColors
import com.twofasapp.core.design.MdtTheme

@Composable
fun PreviewTheme(
    theme: AppTheme = AppTheme.Auto,
    useBackground: Boolean = true,
    content: @Composable () -> Unit,
) {
    PreviewContainer(
        theme = theme,
        content = {
            Box(
                modifier = if (useBackground) Modifier.background(color = MdtTheme.color.background) else Modifier,
            ) {
                content()
            }
        },
    )
}

@Composable
fun PreviewColumn(
    theme: AppTheme = AppTheme.Auto,
    useBackground: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    PreviewContainer(
        theme = theme,
        content = {
            Column(
                modifier = if (useBackground) Modifier.background(color = MdtTheme.color.background) else Modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content,
            )
        },
    )
}

@Composable
fun PreviewRow(
    theme: AppTheme = AppTheme.Auto,
    useBackground: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    PreviewContainer(
        theme = theme,
        content = {
            Row(
                modifier = if (useBackground) Modifier.background(color = MdtTheme.color.background) else Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = content,
            )
        },
    )
}

@Composable
private fun PreviewContainer(
    theme: AppTheme = AppTheme.Auto,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppTheme provides theme,
        LocalDynamicColors provides false,
    ) {
        AppTheme {
            content()
        }
    }
}