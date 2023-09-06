package com.twofasapp.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import com.twofasapp.resources.R

fun Activity.toast(message: String, duration: Int) = Toast.makeText(this, message, duration).show()
fun Activity.toast(@StringRes resId: Int, duration: Int) = Toast.makeText(this, this.resources.getText(resId), duration).show()

fun Activity.toastLong(message: String) = toast(message, Toast.LENGTH_LONG)
fun Activity.toastLong(@StringRes resId: Int) = toast(resId, Toast.LENGTH_LONG)
fun Activity.isNight() = resources.getBoolean(R.bool.isNight)
fun Context.isNight() = resources.getBoolean(R.bool.isNight)

fun Activity.openBrowserApp(urlResId: Int? = null, url: String? = null) {
    val urlToOpen = url ?: urlResId?.let { getString(it) }
    urlToOpen?.let {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        resolveIntent { startActivity(intent) }
    }
}

fun Activity.resolveIntent(action: () -> Unit) {
    try {
        action.invoke()
    } catch (e: Exception) {
        Toast.makeText(this, getString(R.string.errors__no_app), Toast.LENGTH_LONG).show()
    }
}
