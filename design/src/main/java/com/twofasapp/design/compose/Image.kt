package com.twofasapp.design.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.design.theme.isNight
import com.twofasapp.parsers.ServiceIcons

@Composable
fun assetBitmap(fileName: String): ImageBitmap {
    val bitmap = getBitmapFromAssets(LocalContext.current, fileName)
    return bitmap.asImageBitmap()
}

@Composable
fun serviceIconBitmap(iconCollectionId: String): ImageBitmap {
    return assetBitmap(ServiceIcons.getIcon(collectionId = iconCollectionId, isDark = isNight()))
}

fun getBitmapFromAssets(context: Context, fileName: String): Bitmap {
    return try {
        val imageStream = context.assets.open(fileName)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        imageStream.close()
        bitmap
    } catch (e: Exception) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}