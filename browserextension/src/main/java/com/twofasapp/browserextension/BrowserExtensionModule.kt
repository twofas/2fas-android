package com.twofasapp.browserextension

import com.twofasapp.browserextension.data.BrowserExtensionLocalData
import com.twofasapp.browserextension.data.BrowserExtensionLocalDataImpl
import com.twofasapp.browserextension.data.BrowserExtensionRemoteData
import com.twofasapp.browserextension.data.BrowserExtensionRemoteDataImpl
import com.twofasapp.browserextension.domain.*
import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepositoryImpl
import com.twofasapp.browserextension.notification.ShowBrowserExtensionRequestNotificationCaseImpl
import com.twofasapp.browserextension.ui.browser.BrowserDetailsScreenFactory
import com.twofasapp.browserextension.ui.browser.BrowserDetailsViewModel
import com.twofasapp.browserextension.ui.main.BrowserExtensionScreenFactory
import com.twofasapp.browserextension.ui.main.BrowserExtensionViewModel
import com.twofasapp.browserextension.ui.pairing.progress.PairingProgressScreenFactory
import com.twofasapp.browserextension.ui.pairing.progress.PairingProgressViewModel
import com.twofasapp.browserextension.ui.pairing.scan.PairingScanScreenFactory
import com.twofasapp.browserextension.ui.pairing.scan.PairingScanViewModel
import com.twofasapp.browserextension.ui.request.BrowserExtensionRequestViewModel
import com.twofasapp.di.KoinModule
import com.twofasapp.push.domain.ShowBrowserExtensionRequestNotificationCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class BrowserExtensionModule : KoinModule {

    override fun provide() = module {
        singleOf(::BrowserExtensionLocalDataImpl) { bind<BrowserExtensionLocalData>() }
        singleOf(::BrowserExtensionRemoteDataImpl) { bind<BrowserExtensionRemoteData>() }
        singleOf(::BrowserExtensionRepositoryImpl) { bind<BrowserExtensionRepository>() }

        singleOf(::ShowBrowserExtensionRequestNotificationCaseImpl) { bind<ShowBrowserExtensionRequestNotificationCase>() }

        singleOf(::RegisterMobileDeviceCase)
        singleOf(::PairBrowserCase)
        singleOf(::ObserveMobileDeviceCaseImpl) { bind<ObserveMobileDeviceCase>() }
        singleOf(::UpdateMobileDeviceCase)
        singleOf(::ObservePairedBrowsersCaseImpl) { bind<ObservePairedBrowsersCase>() }
        singleOf(::FetchPairedBrowsersCase)
        singleOf(::FetchTokenRequestsCaseImpl) { bind<FetchTokenRequestsCase>() }
        singleOf(::ApproveLoginRequestCase)
        singleOf(::DenyLoginRequestCase)
        singleOf(::EncryptCodeCase)
        singleOf(::DeletePairedBrowserCase)

        viewModelOf(::BrowserExtensionViewModel)
        viewModelOf(::PairingScanViewModel)
        viewModelOf(::PairingProgressViewModel)
        viewModelOf(::BrowserExtensionRequestViewModel)
        viewModelOf(::BrowserDetailsViewModel)

        singleOf(::BrowserExtensionScreenFactory)
        singleOf(::PairingProgressScreenFactory)
        singleOf(::PairingScanScreenFactory)
        singleOf(::BrowserDetailsScreenFactory)
    }
}