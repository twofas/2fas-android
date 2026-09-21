/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
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
fun InfoDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    positive: String = MdtLocale.strings.commonOk,
    negative: String? = null,
    neutral: String? = null,
    icon: Painter? = null,
    iconColor: Color = Color.Unspecified,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
    onNeutral: () -> Unit = {},
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    neutralColor: Color = Color.Unspecified,
    actionsAlignment: ActionsAlignment = ActionsAlignment.Horizontal,
    properties: DialogProperties = DialogProperties(),
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        bodyAnnotated = bodyAnnotated,
        positive = positive,
        negative = negative,
        neutral = neutral,
        icon = icon,
        iconColor = iconColor,
        onPositiveClick = onPositive,
        onNegativeClick = onNegative,
        onNeutralClick = onNeutral,
        positiveColor = positiveColor,
        negativeColor = negativeColor,
        neutralColor = neutralColor,
        properties = properties,
        actionsAlignment = actionsAlignment,
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        InfoDialog(
            onDismissRequest = { },
            title = "Info",
            body = PreviewTextLong,
        )
    }
}