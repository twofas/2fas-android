package com.twofasapp.designsystem.ktx

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.core.app.ComponentActivity
import com.twofasapp.locale.R
import com.twofasapp.locale.Strings
import com.twofasapp.locale.TwLocale

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

val Context.settingsIntent: Intent
    get() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

val CompositionLocal<Context>.strings: Strings
    @Composable
    get() = TwLocale.strings

val LocalBackDispatcher
    @Composable
    get() = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher!!

fun Context.copyToClipboard(
    text: String,
    label: String = "Text",
    toast: String = "Copied!",
    isSensitive: Boolean = false,
    showToast: Boolean = true,
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

    val manufacturer = Build.MANUFACTURER.lowercase()
    val shouldShowToast = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2
            || manufacturer.contains("xiaomi")
            || manufacturer.contains("redmi")

    if (shouldShowToast && showToast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

fun UriHandler.openSafely(uri: String, context: Context? = null) {
    try {
        openUri(uri)
    } catch (e: Exception) {
        context?.let { Toast.makeText(it, it.getText(R.string.errors__no_app), Toast.LENGTH_SHORT).show() }
    }
}

fun Context.toast(message: String, duration: Int) = Toast.makeText(this, message, duration).show()
fun Context.toast(@StringRes resId: Int, duration: Int) = Toast.makeText(this, this.resources.getText(resId), duration).show()

fun Context.toastLong(message: String) = toast(message, Toast.LENGTH_LONG)
fun Context.toastLong(@StringRes resId: Int) = toast(resId, Toast.LENGTH_LONG)

fun Context.toastShort(message: String) = toast(message, Toast.LENGTH_SHORT)
fun Context.toastShort(@StringRes resId: Int) = toast(resId, Toast.LENGTH_SHORT)

sealed interface ConnectionState {
    data object Available : ConnectionState
    data object Unavailable : ConnectionState
}

val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): ConnectionState {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) ConnectionState.Available else ConnectionState.Unavailable
}

fun Activity.makeWindowSecure(allow: Boolean) {
    if (allow) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}
