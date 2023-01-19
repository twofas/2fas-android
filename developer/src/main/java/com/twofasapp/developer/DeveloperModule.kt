package com.twofasapp.developer

import com.twofasapp.developer.domain.ObserveLastPushesCase
import com.twofasapp.developer.domain.ObserveLastPushesCaseImpl
import com.twofasapp.developer.domain.ObserveLastScannedQrCase
import com.twofasapp.developer.domain.ObserveLastScannedQrCaseImpl
import com.twofasapp.developer.domain.repository.DeveloperRepository
import com.twofasapp.developer.domain.repository.DeveloperRepositoryImpl
import com.twofasapp.developer.ui.DeveloperViewModel
import com.twofasapp.di.KoinModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DeveloperModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::DeveloperViewModel)

        singleOf(::DeveloperRepositoryImpl) { bind<DeveloperRepository>() }

        singleOf(::ObserveLastPushesCaseImpl) { bind<ObserveLastPushesCase>() }
        singleOf(::ObserveLastScannedQrCaseImpl) { bind<ObserveLastScannedQrCase>() }
    }
}