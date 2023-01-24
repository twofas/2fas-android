package com.twofasapp.designsystem.ktx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.IOException

private val emptyBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

fun Context.assetAsBitmap(fileName: String): Bitmap {
    return try {
        with(assets.open(fileName)) {
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) {
        emptyBitmap
    }
}

@Composable
fun assetAsBitmap(fileName: String): ImageBitmap {
    return LocalContext.current.assetAsBitmap(fileName).asImageBitmap()
}