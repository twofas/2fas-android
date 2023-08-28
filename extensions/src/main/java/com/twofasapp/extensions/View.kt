package com.twofasapp.extensions

import android.view.View
import android.view.ViewTreeObserver

fun View.onClick(listener: (View) -> Unit) {
    setOnClickListener(listener)
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.visible(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}