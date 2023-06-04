package com.twofasapp.feature.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.qrscan.ReadQrFromImage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class QrScanModule : KoinModule {

    override fun provide() = module {
        singleOf(::ReadQrFromImage)
    }
}