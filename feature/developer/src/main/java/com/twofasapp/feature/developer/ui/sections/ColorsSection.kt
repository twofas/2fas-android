/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.theme.ColorTokens

@Composable
internal fun ColorsSection() {
    val color = MdtTheme.color
    val swatches = colorSwatches(color)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Colors",
            style = MdtTheme.typo.base.semiBold,
            color = MdtTheme.color.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        swatches.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (name, value) ->
                    ColorSwatch(
                        name = name,
                        color = value,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val textColor = if (color.luminance() < 0.5f) Color.White else Color.Black

    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(1.dp, MdtTheme.color.outlineVariant, RoundedCornerShape(8.dp))
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = name,
                style = MdtTheme.typo.xs2.medium,
                color = textColor,
            )
            Text(
                text = color.toHex(),
                style = MdtTheme.typo.xs4.normal,
                color = textColor.copy(alpha = 0.7f),
            )
        }
    }
}

private fun Color.toHex(): String {
    return "#%08X".format(toArgb())
}

private fun colorSwatches(c: ColorTokens): List<Pair<String, Color>> {
    return listOf(
        "primary" to c.primary,
        "onPrimary" to c.onPrimary,
        "primaryContainer" to c.primaryContainer,
        "onPrimaryContainer" to c.onPrimaryContainer,
        "secondary" to c.secondary,
        "onSecondary" to c.onSecondary,
        "secondaryContainer" to c.secondaryContainer,
        "onSecondaryContainer" to c.onSecondaryContainer,
        "tertiary" to c.tertiary,
        "onTertiary" to c.onTertiary,
        "tertiaryContainer" to c.tertiaryContainer,
        "onTertiaryContainer" to c.onTertiaryContainer,
        "error" to c.error,
        "onError" to c.onError,
        "errorContainer" to c.errorContainer,
        "onErrorContainer" to c.onErrorContainer,
        "background" to c.background,
        "onBackground" to c.onBackground,
        "surface" to c.surface,
        "onSurface" to c.onSurface,
        "surfaceVariant" to c.surfaceVariant,
        "onSurfaceVariant" to c.onSurfaceVariant,
        "outline" to c.outline,
        "outlineVariant" to c.outlineVariant,
        "scrim" to c.scrim,
        "inverseSurface" to c.inverseSurface,
        "inverseOnSurface" to c.inverseOnSurface,
        "inversePrimary" to c.inversePrimary,
        "surfaceDim" to c.surfaceDim,
        "surfaceBright" to c.surfaceBright,
        "surfaceContainerLowest" to c.surfaceContainerLowest,
        "surfaceContainerLow" to c.surfaceContainerLow,
        "surfaceContainer" to c.surfaceContainer,
        "surfaceContainerHigh" to c.surfaceContainerHigh,
        "surfaceContainerHighest" to c.surfaceContainerHighest,
        "divider" to c.divider,
        "iconTint" to c.iconTint,
        "accentLightBlue" to c.accentLightBlue,
        "accentIndigo" to c.accentIndigo,
        "accentPurple" to c.accentPurple,
        "accentTurquoise" to c.accentTurquoise,
        "accentGreen" to c.accentGreen,
        "accentOrange" to c.accentOrange,
        "accentYellow" to c.accentYellow,
        "accentPink" to c.accentPink,
        "accentBrown" to c.accentBrown,
    )
}