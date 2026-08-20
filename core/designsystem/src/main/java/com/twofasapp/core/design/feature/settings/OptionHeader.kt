/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.feature.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewTheme

val MdtOptionHeaderContentPadding = PaddingValues(
    start = 16.dp,
    top = 28.dp,
    end = 16.dp,
    bottom = 8.dp,
)

val OptionHeaderContentPaddingFirst = PaddingValues(
    start = 16.dp,
    top = 12.dp,
    end = 16.dp,
    bottom = 8.dp,
)

@Composable
fun OptionHeader(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = MdtOptionHeaderContentPadding,
) {
    Text(
        text = text,
        style = MdtTheme.typo.sm.medium,
        color = MdtTheme.color.primary,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .padding(contentPadding),
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        OptionHeader(text = "Header")
    }
}