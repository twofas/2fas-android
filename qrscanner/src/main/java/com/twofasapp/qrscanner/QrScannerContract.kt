package com.twofasapp.qrscanner

import com.twofasapp.base.BasePresenter

interface QrScannerContract {

    interface View

    abstract class Presenter : BasePresenter() {
        abstract fun onScanned(text: String)
    }
}