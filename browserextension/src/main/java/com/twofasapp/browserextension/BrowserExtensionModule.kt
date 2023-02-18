package com.twofasapp.browserextension

import com.twofasapp.browserextension.domain.ApproveLoginRequestCase
import com.twofasapp.browserextension.domain.DeletePairedBrowserCase
import com.twofasapp.browserextension.domain.DenyLoginRequestCase
import com.twofasapp.browserextension.domain.EncryptCodeCase
import com.twofasapp.browserextension.domain.FetchPairedBrowsersCase
import com.twofasapp.browserextension.domain.FetchTokenRequestsCase
import com.twofasapp.browserextension.domain.ObserveMobileDeviceCase
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.browserextension.domain.PairBrowserCase
import com.twofasapp.browserextension.domain.RegisterMobileDeviceCase
import com.twofasapp.browserextension.domain.UpdateMobileDeviceCase
import com.twofasapp.browserextension.notification.ShowBrowserExtensionRequestNotificationCaseImpl
import com.twofasapp.browserextension.ui.browser.BrowserDetailsViewModel
import com.twofasapp.browserextension.ui.main.BrowserExtensionViewModel
import com.twofasapp.browserextension.ui.pairing.progress.PairingProgressViewModel
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
        singleOf(::ShowBrowserExtensionRequestNotificationCaseImpl) { bind<ShowBrowserExtensionRequestNotificationCase>() }

        singleOf(::RegisterMobileDeviceCase)
        singleOf(::PairBrowserCase)
        singleOf(::ObserveMobileDeviceCase)
        singleOf(::UpdateMobileDeviceCase)
        singleOf(::ObservePairedBrowsersCase)
        singleOf(::FetchPairedBrowsersCase)
        singleOf(::FetchTokenRequestsCase)
        singleOf(::ApproveLoginRequestCase)
        singleOf(::DenyLoginRequestCase)
        singleOf(::EncryptCodeCase)
        singleOf(::DeletePairedBrowserCase)

        viewModelOf(::BrowserExtensionViewModel)
        viewModelOf(::BrowserExtensionPermissionViewModel)
        viewModelOf(::PairingScanViewModel)
        viewModelOf(::PairingProgressViewModel)
        viewModelOf(::BrowserExtensionRequestViewModel)
        viewModelOf(::BrowserDetailsViewModel)
    }
}