package com.twofasapp.designsystem.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.locale.TwLocale

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
        positive = positiveText ?: TwLocale.strings.settingsSettings,
        negative = negativeText ?: TwLocale.strings.commonCancel,
        onPositiveClick = {
            if (onPositive != null) {
                onPositive.invoke()
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onDismissRequest()
            }
        },
        onNegativeClick = onNegative ?: {},
    )
}