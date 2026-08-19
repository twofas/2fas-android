/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.locale.MdtLocale

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    positive: String = MdtLocale.strings.commonYes,
    // Relaxed to nullable vs the reference: some call sites pass negative = null to render a
    // single-button confirm (BaseDialog only shows the button when non-null).
    negative: String? = MdtLocale.strings.commonNo,
    icon: Painter? = null,
    iconColor: Color = Color.Unspecified,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    actionsAlignment: ActionsAlignment = ActionsAlignment.Horizontal,
    properties: DialogProperties = DialogProperties(),
) {
    InfoDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        bodyAnnotated = bodyAnnotated,
        positive = positive,
        negative = negative,
        icon = icon,
        iconColor = iconColor,
        onPositive = onPositive,
        onNegative = onNegative,
        positiveColor = positiveColor,
        negativeColor = negativeColor,
        properties = properties,
        actionsAlignment = actionsAlignment,
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        ConfirmDialog(
            onDismissRequest = { },
            title = "Confirm?",
            body = PreviewTextLong,
        )
    }
}