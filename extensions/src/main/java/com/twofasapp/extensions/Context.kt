package com.twofasapp.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

@ColorInt
fun Context.getColorFromRes(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

@DrawableRes
fun Context.getDrawableFromAttr(@AttrRes attributeDrawable: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(attributeDrawable, value, true)
    return value.resourceId
}

fun Context.getString(text: String? = null, textRes: Int? = null): String? {
    when {
        text == null && textRes == null -> return null
        text != null -> return text
        textRes != null && textRes != 0 -> return getString(textRes)
        else -> return null
    }
}