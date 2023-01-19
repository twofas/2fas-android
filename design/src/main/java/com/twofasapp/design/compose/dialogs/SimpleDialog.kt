package com.twofasapp.design.compose.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.dialogs.internal.BaseDialog
import com.twofasapp.design.compose.dialogs.internal.BaseDialogTextContent

@Composable
fun SimpleDialog(
    title: String? = null,
    text: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    icon: Painter? = null,
    iconTint: Color? = null,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
    content: @Composable (() -> Unit)? = null,
) {
    BaseDialog(
        title = title,
        positiveText = positiveText,
        negativeText = negativeText,
        icon = icon,
        iconTint = iconTint,
        onPositive = onPositive,
        onNegative = onNegative,
        onDismiss = onDismiss,
    ) {
        Column {
            if (text != null) {
                BaseDialogTextContent(text = text)
            }

            if (content != null) {
                Spacer(modifier = Modifier.height(24.dp))
                content.invoke()
            }
        }
    }
}