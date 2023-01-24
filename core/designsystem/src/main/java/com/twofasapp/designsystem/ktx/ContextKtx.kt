package com.twofasapp.designsystem.ktx

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.core.app.ComponentActivity

val CompositionLocal<Context>.currentActivity: ComponentActivity
    @Composable
    get() {
        var context = this.current

        while (context is ContextWrapper) {
            if (context is ComponentActivity) return context
            context = context.baseContext
        }

        error("No component activity")
    }

val LocalBackDispatcher
    @Composable
    get() = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

fun Context.copyToClipboard(text: String, label: String = "Text", toast: String = "Copied!") {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clipData)

    Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
}