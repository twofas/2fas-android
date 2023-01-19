package com.twofasapp.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.ColorInt
import java.io.IOException

val emptyBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

fun ImageView.tint(@ColorInt color: Int, mode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN) {
    setColorFilter(color, mode)
}

fun ImageView.loadAsset(fileName: String) {
    try {
        with(context.assets.open(fileName)) {
            setImageBitmap(BitmapFactory.decodeStream(this))
        }
    } catch (e: IOException) {
        setImageBitmap(emptyBitmap)
    }
}