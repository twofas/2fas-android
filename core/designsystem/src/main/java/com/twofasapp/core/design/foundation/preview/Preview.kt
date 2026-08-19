/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.LocalAppTheme
import com.twofasapp.core.design.LocalDynamicColors
import com.twofasapp.core.design.MdtTheme

@Composable
fun PreviewTheme(
    appTheme: AppTheme = AppTheme.Dark,
    useBackground: Boolean = true,
    color: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppTheme provides appTheme,
        LocalDynamicColors provides false,
    ) {
        AppTheme {
            Surface(color = if (useBackground) MdtTheme.color.background else color) {
                content()
            }
        }
    }
}

@Composable
fun PreviewAllThemesInRow(
    useBackground: Boolean = true,
    color: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    Row {
        PreviewColumn(theme = AppTheme.Dark) {
            content()
        }

        Spacer(modifier = Modifier.width(16.dp))

        PreviewColumn(theme = AppTheme.Light) {
            content()
        }
    }
}

@Composable
fun PreviewAllThemesInColumn(
    useBackground: Boolean = true,
    color: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    Column {
        PreviewRow(theme = AppTheme.Dark) {
            content()
        }

        Spacer(modifier = Modifier.width(16.dp))

        PreviewRow(theme = AppTheme.Light) {
            content()
        }
    }
}

@Composable
fun PreviewColumn(
    modifier: Modifier = Modifier,
    theme: AppTheme = AppTheme.Dark,
    useBackground: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    PreviewTheme(
        appTheme = theme,
        useBackground = useBackground,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
            modifier = modifier,
        )
    }
}

@Composable
fun PreviewRow(
    modifier: Modifier = Modifier,
    theme: AppTheme = AppTheme.Dark,
    useBackground: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    PreviewTheme(
        appTheme = theme,
        useBackground = useBackground,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
            modifier = modifier,
        )
    }
}