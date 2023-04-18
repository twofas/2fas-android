package com.twofasapp.designsystem.ktx

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
    get() = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher!!

fun Context.copyToClipboard(
    text: String,
    label: String = "Text",
    toast: String = "Copied!",
    isSensitive: Boolean = false,
) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text).apply {
        if (isSensitive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                description.extras = PersistableBundle().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                    } else {
                        putBoolean("android.content.extra.IS_SENSITIVE", true)
                    }
                }
            }
        }
    }
    clipboardManager.setPrimaryClip(clipData)

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }