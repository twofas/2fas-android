package com.twofasapp.core.design.foundation.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.ktx.copyToClipboard

@Composable
fun StackTraceDetails(
    modifier: Modifier = Modifier,
    title: String,
    content: String?,
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (content == null) return

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(
            text = title,
            style = ButtonStyle.Text,
            modifier = Modifier,
            onClick = { showDialog = true },
        )
    }

    if (showDialog) {
        InfoDialog(
            onDismissRequest = { showDialog = false },
            title = "Error details",
            positive = "Ok",
            negative = "Copy",
            onNegative = { context.copyToClipboard(content) },
            body = content,
        )
    }
}

fun Throwable.formatErrorDetails(): String {
    return buildString {
        append("Fatal Exception: ${this@formatErrorDetails.javaClass.name}")
        append("\n")
        append("\n")
        append(stackTrace.joinToString("\n"))
    }
}