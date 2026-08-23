package com.twofasapp.feature.browserext.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.browserext.ui.details.BrowserExtDetailsViewModel
import com.twofasapp.feature.browserext.ui.main.BrowserExtViewModel
import com.twofasapp.feature.browserext.ui.pairing.BrowserExtPairingViewModel
import com.twofasapp.feature.browserext.ui.permission.BrowserExtPermissionViewModel
import com.twofasapp.feature.browserext.ui.request.BrowserExtRequestViewModel
import com.twofasapp.feature.browserext.ui.scan.BrowserExtScanViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class BrowserExtModule : KoinModule {

    override fun provide(): Module = module {
        viewModel { params -> BrowserExtDetailsViewModel(extensionId = params.get(), browserExtRepository = get()) }
        viewModelOf(::BrowserExtViewModel)
        viewModelOf(::BrowserExtPermissionViewModel)
        viewModel { params -> BrowserExtPairingViewModel(extensionId = params.get(), browserExtRepository = get(), appBuild = get()) }
        viewModelOf(::BrowserExtScanViewModel)
        viewModelOf(::BrowserExtRequestViewModel)
    }
}