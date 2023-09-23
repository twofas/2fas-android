package com.twofasapp.feature.browserext.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.browserext.notification.ShowBrowserExtRequestNotificationCaseImpl
import com.twofasapp.feature.browserext.ui.details.BrowserExtDetailsViewModel
import com.twofasapp.feature.browserext.ui.main.BrowserExtViewModel
import com.twofasapp.feature.browserext.ui.pairing.BrowserExtPairingViewModel
import com.twofasapp.feature.browserext.ui.permission.BrowserExtPermissionViewModel
import com.twofasapp.feature.browserext.ui.request.BrowserExtRequestViewModel
import com.twofasapp.feature.browserext.ui.scan.BrowserExtScanViewModel
import com.twofasapp.push.domain.ShowBrowserExtRequestNotificationCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class BrowserExtModule : KoinModule {

    override fun provide(): Module = module {
        singleOf(::ShowBrowserExtRequestNotificationCaseImpl) { bind<ShowBrowserExtRequestNotificationCase>() }

        viewModelOf(::BrowserExtDetailsViewModel)
        viewModelOf(::BrowserExtViewModel)
        viewModelOf(::BrowserExtPermissionViewModel)
        viewModelOf(::BrowserExtPairingViewModel)
        viewModelOf(::BrowserExtScanViewModel)
        viewModelOf(::BrowserExtRequestViewModel)
    }
}