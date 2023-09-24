package com.twofasapp.qrscanner

import com.twofasapp.common.di.KoinModule
import com.twofasapp.qrscanner.domain.ReadQrFromImageCase
import com.twofasapp.qrscanner.domain.ScanQr
import com.twofasapp.qrscanner.ui.QrScannerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class QrScannerModule : KoinModule {
    override fun provide() = module {
        single { ScanQr() }
        single { ReadQrFromImageCase(androidContext()) }

        viewModelOf(::QrScannerViewModel)
    }
}