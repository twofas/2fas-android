package com.twofasapp.extensions

import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.RectF
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.twofasapp.extensions.R
import kotlin.math.roundToInt

fun AppCompatActivity.replaceFragment(layoutRes: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .replace(layoutRes, fragment)
        .commit()
}


fun Activity.makeWindowSecure() {
    if (BuildConfig.BUILD_TYPE.equals("release", true)) {
        window?.setFlags(
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

val Activity.screenRectPx: Rect
    get() = Resources.getSystem().displayMetrics.run { Rect(0, 0, widthPixels, heightPixels) }

val Activity.screenRectDp: RectF
    get() = Resources.getSystem().displayMetrics.run {
        RectF(
            0f,
            0f,
            widthPixels.px2dp,
            heightPixels.px2dp
        )
    }

val Number.px2dp: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

val Number.dp2px: Int
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).roundToInt()

inline fun <R> String?.ifNotBlank(let: (String) -> R): R? =
    if (isNullOrBlank()) null else let.invoke(this)

