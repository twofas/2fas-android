package com.twofasapp.qrscanner.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Scheduler
import io.reactivex.Single

class ReadQrFromImageRx(private val context: Context) : UseCaseParameterized<Uri, Single<ReadQrFromImageRx.Result>> {

    sealed class Result {
        data class Success(val content: String) : Result()
        data class Error(val throwable: Throwable) : Result()
    }

    override fun execute(params: Uri, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<Result> {
        return Single.create<Result> { emmiter ->

            try {
                val image = InputImage.fromFilePath(context, params)
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
                            emmiter.onSuccess(Result.Success(text))
                        } ?: emmiter.onSuccess(Result.Error(RuntimeException()))
                    }
                    .addOnFailureListener {
                        emmiter.onSuccess(Result.Error(it))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                emmiter.onSuccess(Result.Error(e))
            }
        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}