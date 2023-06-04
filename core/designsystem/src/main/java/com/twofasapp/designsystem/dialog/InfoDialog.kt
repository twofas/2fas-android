package com.twofasapp.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.locale.TwLocale

@Composable
fun InfoDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    positive: String? = TwLocale.strings.commonOk,
    negative: String? = null,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        bodyAnnotated = bodyAnnotated,
        positive = positive,
        negative = negative,
        onPositiveClick = onPositive,
        onNegativeClick = onNegative,
        properties = properties,
    )
}

@Preview
@Composable
private fun Preview() {
    InfoDialog(
        onDismissRequest = { },
        title = "Confirm?",
        body = TwLocale.strings.placeholderLong,
    )
}