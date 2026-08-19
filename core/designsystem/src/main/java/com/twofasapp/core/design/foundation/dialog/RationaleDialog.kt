package com.twofasapp.core.design.foundation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.core.design.ktx.settingsIntent
import com.twofasapp.locale.MdtLocale

@Composable
fun RationaleDialog(
    title: String? = null,
    text: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
) {
    val context = LocalContext.current

    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = text,
        positive = positiveText ?: MdtLocale.strings.settingsSettings,
        negative = negativeText ?: MdtLocale.strings.commonCancel,
        onPositiveClick = {
            if (onPositive != null) {
                onPositive.invoke()
            } else {
                context.startActivity(context.settingsIntent)
                onDismissRequest()
            }
        },
        onNegativeClick = onNegative ?: {},
    )
}