/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.button

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.modifiers.thenIfTrue
import com.twofasapp.core.design.foundation.preview.PreviewColumn
import com.twofasapp.core.design.foundation.text.TextIcon

enum class ButtonStyle { Filled, Outlined, Text, Tonal }
enum class ButtonHeight { Default, Small }

@Composable
fun Button(
    modifier: Modifier = Modifier,
    text: String? = null,
    content: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    style: ButtonStyle = ButtonStyle.Filled,
    size: ButtonHeight = when (style) {
        ButtonStyle.Filled -> ButtonHeight.Default
        ButtonStyle.Outlined -> ButtonHeight.Default
        ButtonStyle.Text -> ButtonHeight.Small
        ButtonStyle.Tonal -> ButtonHeight.Default
    },
    clickable: Boolean = true,
    enabled: Boolean = true,
    loading: Boolean = false,
    contentPadding: PaddingValues? = null,
    height: Dp? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    leadingIcon: Painter? = null,
    leadingIconUrl: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val isIconSet = leadingIcon != null || leadingIconUrl != null

    val padding = contentPadding ?: when (style) {
        ButtonStyle.Filled -> PaddingValues(horizontal = if (isIconSet) 16.dp else 24.dp)
        ButtonStyle.Outlined -> PaddingValues(horizontal = if (isIconSet) 16.dp else 24.dp)
        ButtonStyle.Text -> PaddingValues(horizontal = 12.dp)
        ButtonStyle.Tonal -> PaddingValues(horizontal = if (isIconSet) 16.dp else 24.dp)
    }

    Button(
        onClick = {
            if (clickable && enabled && loading.not()) {
                onClick()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor ?: style.containerColor,
            contentColor = contentColor ?: style.contentColor,
            disabledContainerColor = containerColor ?: style.disabledContainerColor,
            disabledContentColor = contentColor ?: style.disabledContentColor,
        ),
        shape = CircleShape,
        enabled = enabled && loading.not(),
        modifier = modifier
            .thenIfTrue(
                condition = style == ButtonStyle.Outlined,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (enabled) MdtTheme.color.outline else MdtTheme.color.onSurface12,
                    shape = CircleShape,
                ),
            )
            .height(
                height ?: when (size) {
                    ButtonHeight.Default -> 46.dp
                    ButtonHeight.Small -> 40.dp
                },
            ),
        contentPadding = padding,
        interactionSource = interactionSource,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = contentColor ?: if (enabled) MdtTheme.color.primary else MdtTheme.color.primary.copy(alpha = 0.5f),
            )
        } else {
            if (text != null) {
                TextIcon(
                    text = text,
                    // Adapted: reference used MdtTheme.typo.labelLarge (absent here); medium.sm is an
                    // exact match (14sp / 20sp / FontWeight 500).
                    style = MdtTheme.typo.medium.sm,
                    color = contentColor ?: if (enabled) style.contentColor else style.disabledContentColor,
                    leadingIcon = leadingIcon,
                    leadingIconUrl = leadingIconUrl,
                    leadingIconTint = contentColor ?: if (enabled) style.contentColor else style.disabledContentColor,
                    leadingIconSpacer = 6.dp,
                    leadingIconSize = 18.dp,
                )
            } else {
                content?.invoke()
            }
        }
    }
}

private val ButtonStyle.containerColor: Color
    @Composable
    get() = when (this) {
        ButtonStyle.Filled -> MdtTheme.color.primary
        ButtonStyle.Outlined -> MdtTheme.color.transparent
        ButtonStyle.Text -> MdtTheme.color.transparent
        ButtonStyle.Tonal -> MdtTheme.color.secondaryContainer
    }

private val ButtonStyle.disabledContainerColor: Color
    @Composable
    get() = when (this) {
        ButtonStyle.Filled -> MdtTheme.color.onSurface12
        ButtonStyle.Outlined -> MdtTheme.color.transparent
        ButtonStyle.Text -> MdtTheme.color.transparent
        ButtonStyle.Tonal -> MdtTheme.color.onSurface12
    }

private val ButtonStyle.contentColor: Color
    @Composable
    get() = when (this) {
        ButtonStyle.Filled -> MdtTheme.color.onPrimary
        ButtonStyle.Outlined -> MdtTheme.color.primary
        ButtonStyle.Text -> MdtTheme.color.primary
        ButtonStyle.Tonal -> MdtTheme.color.onSurface
    }

private val ButtonStyle.disabledContentColor: Color
    @Composable
    get() = when (this) {
        ButtonStyle.Filled -> MdtTheme.color.onSurface.copy(alpha = 0.38f)
        ButtonStyle.Outlined -> MdtTheme.color.onSurface.copy(alpha = 0.38f)
        ButtonStyle.Text -> MdtTheme.color.onSurface.copy(alpha = 0.38f)
        ButtonStyle.Tonal -> MdtTheme.color.onSurface.copy(alpha = 0.38f)
    }

@Preview
@Composable
private fun Preview() {
    PreviewColumn {
        ButtonStyle.entries.forEach { style ->
            Button(
                text = style.name,
                enabled = true,
                style = style,
                leadingIcon = MdtIcons.Add,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDisabled() {
    PreviewColumn {
        ButtonStyle.entries.forEach { style ->
            Button(
                text = style.name,
                enabled = false,
                style = style,
                leadingIcon = MdtIcons.Add,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    PreviewColumn {
        ButtonStyle.entries.forEach { style ->
            Button(
                text = style.name,
                loading = true,
                style = style,
            )
        }
    }
}