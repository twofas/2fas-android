package com.twofasapp.extensions

import android.app.Activity
import android.content.res.Resources
import android.view.WindowManager
import com.twofasapp.extensions.R
import kotlin.math.roundToInt

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


fun doNothing() = Unit

fun String.splitIntoLetters(): String = replace(".".toRegex(), "$0 ")

fun String.splitForSpelling(): String = replace(".".toRegex(), "$0. ")

fun String.insert(insertAt: Int, string: String): String {
    return this.substring(0, insertAt) + string + this.substring(insertAt, this.length)
}

fun String.removeWhiteCharacters(): String {
    return replace(" ", "")
}

val Number.px2dp: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

val Number.dp2px: Int
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).roundToInt()

inline fun <R> String?.ifNotBlank(let: (String) -> R): R? =
    if (isNullOrBlank()) null else let.invoke(this)

