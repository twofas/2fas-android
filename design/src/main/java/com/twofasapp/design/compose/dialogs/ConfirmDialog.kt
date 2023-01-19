package com.twofasapp.design.compose.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.twofasapp.resources.R

@Composable
fun ConfirmDialog(
    title: String? = null,
    text: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
) {

    SimpleDialog(
        title = title,
        text = text,
        positiveText = positiveText ?: stringResource(id = R.string.commons__yes),
        negativeText = negativeText ?: stringResource(id = R.string.commons__no),
        onPositive = onPositive ?: {},
        onNegative = onNegative ?: {},
        onDismiss = onDismiss,
    )
}
