package com.twofasapp.extensions

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat

fun ImageView.setTintRes(@ColorRes colorRes: Int) {
    if (colorRes == 0) return

    val color = ContextCompat.getColor(context, colorRes)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}

fun ImageView.setTint(@ColorInt color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun View.setBackgroundTint(@ColorInt color: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(color))
}

fun View.animateGone() {
    if (visibility != View.GONE) {
        animate().setDuration(150).alpha(0f).withEndAction { makeGone() }.start()
    }
}

fun View.animateInvisible() {
    if (visibility != View.GONE) {
        animate().setDuration(150).alpha(0f).withEndAction { makeInvisible() }.start()
    }
}