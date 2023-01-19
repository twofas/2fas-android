package com.twofasapp.qrscanner.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ReadQrFromImageCase(private val context: Context) {

    sealed class Result {
        data class Success(val content: String) : Result()
        data class Error(val throwable: Throwable) : Result()
    }

    suspend operator fun invoke(uri: Uri): Result = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromFilePath(context, uri)
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                    ).build()
            )
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.getOrNull(0)
                    barcode?.rawValue?.let { text ->
                        continuation.resume(Result.Success(text))
                    } ?: continuation.resume(Result.Error(RuntimeException()))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Error(it))
                }

        } catch (e: Exception) {
            e.printStackTrace()

            continuation.resume(Result.Error(e))
        }
    }
}