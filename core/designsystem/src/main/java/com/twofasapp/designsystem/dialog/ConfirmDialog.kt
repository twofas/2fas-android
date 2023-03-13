package com.twofasapp.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.locale.TwLocale

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    onConfirm: () -> Unit = {},
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        positive = TwLocale.strings.commonYes,
        negative = TwLocale.strings.commonNo,
        onPositiveClick = onConfirm,
    )
}

@Preview
@Composable
private fun Preview() {
    ConfirmDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Confirm?",
        body = TwLocale.strings.placeholderLong,
    )
}