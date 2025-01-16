package com.twofasapp.feature.home.ui.editservice

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrGenerator {
    fun generateBitmap(content: String): Bitmap {
        val size = 768

        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
        )

        val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}