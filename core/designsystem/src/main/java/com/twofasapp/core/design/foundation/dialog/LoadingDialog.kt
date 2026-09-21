/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.core.design.foundation.preview.PreviewText
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator

@Composable
fun LoadingDialog(
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit = {},
    title: String? = null,
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        contentScrollable = false,
        title = title,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        negative = "Cancel",
        onNegativeClick = onCancelClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Adapted: reference used CircularProgress(); this repo ships CircularProgressIndicator().
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        LoadingDialog(
            onDismissRequest = {},
            title = PreviewText,
        )
    }
}