package com.twofasapp.core.design.ktx

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.locale.MdtLocale

/**
 * Returns a function that opens the system file picker for the given mime types. Some devices ship without an app
 * handling ACTION_OPEN_DOCUMENT, so it falls back to ACTION_GET_CONTENT, and shows an error (reporting a null
 * result) when neither is available.
 */
@Composable
fun rememberFilePicker(onResult: (Uri?) -> Unit): (mimeTypes: Array<String>) -> Unit {
    val context = LocalContext.current
    val errorMessage = MdtLocale.strings.errorUnknown
    val currentOnResult by rememberUpdatedState(onResult)

    val openDocumentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        currentOnResult(it)
    }
    val getContentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        currentOnResult(it)
    }

    return remember(openDocumentLauncher, getContentLauncher, errorMessage) {
        { mimeTypes ->
            try {
                openDocumentLauncher.launch(mimeTypes)
            } catch (_: ActivityNotFoundException) {
                try {
                    // GET_CONTENT takes a single type.
                    getContentLauncher.launch(mimeTypes.singleOrNull() ?: "*/*")
                } catch (_: ActivityNotFoundException) {
                    context.toastShort(errorMessage)
                    currentOnResult(null)
                }
            }
        }
    }
}