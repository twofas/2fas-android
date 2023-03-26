package com.twofasapp.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.locale.TwLocale

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    positive: String? = TwLocale.strings.commonYes,
    negative: String? = TwLocale.strings.commonNo,
    onConfirm: () -> Unit = {},
    onDeny: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        positive = positive,
        negative = negative,
        onPositiveClick = onConfirm,
        onNegativeClick = onDeny,
        properties = properties,
    )
}

@Preview
@Composable
private fun Preview() {
    ConfirmDialog(
        onDismissRequest = { },
        title = "Confirm?",
        body = TwLocale.strings.placeholderLong,
    )
}