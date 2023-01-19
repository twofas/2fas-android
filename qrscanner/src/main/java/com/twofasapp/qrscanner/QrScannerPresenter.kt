package com.twofasapp.qrscanner

import com.twofasapp.qrscanner.domain.ScanQr
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class QrScannerPresenter(
    private val view: QrScannerContract.View,
    private val scanQr: ScanQr,
) : QrScannerContract.Presenter() {

    private var isAcceptingImage = true

    override fun onViewAttached() {
        resetScanner()

        scanQr.observeReset()
            .safelySubscribe(onNext = { resetWithDelay() }, onError = { resetWithDelay() })
    }

    private fun resetWithDelay() {
        Completable.complete()
            .delay(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .safelySubscribe { resetScanner() }
    }

    override fun onScanned(text: String) {
        if (isAcceptingImage) {
            scanQr.publishResult(ScanQr.Result(text))
            isAcceptingImage = false
            Timber.d(text)
        }
    }

    private fun resetScanner() {
        isAcceptingImage = true
    }
}
