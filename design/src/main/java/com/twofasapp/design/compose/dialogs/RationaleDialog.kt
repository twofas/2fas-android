package com.twofasapp.design.compose.dialogs

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twofasapp.resources.R

@Composable
fun RationaleDialog(
    title: String? = null,
    text: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
) {

    val context = LocalContext.current

    SimpleDialog(
        title = title,
        text = text,
        positiveText = positiveText ?: stringResource(id = R.string.settings__settings),
        negativeText = negativeText ?: stringResource(id = R.string.commons__cancel),
        onPositive = {
            if (onPositive != null) {
                onPositive.invoke()
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                onDismiss.invoke()
            }
        },
        onNegative = onNegative ?: {},
        onDismiss = onDismiss,
    )
}
