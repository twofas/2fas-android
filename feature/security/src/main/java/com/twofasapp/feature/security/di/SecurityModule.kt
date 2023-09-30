package com.twofasapp.feature.security.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.security.ui.changepin.ChangePinViewModel
import com.twofasapp.feature.security.ui.disablepin.DisablePinViewModel
import com.twofasapp.feature.security.ui.lock.LockViewModel
import com.twofasapp.feature.security.ui.security.SecurityViewModel
import com.twofasapp.feature.security.ui.setuppin.SetupPinViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class SecurityModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::SecurityViewModel)
        viewModelOf(::SetupPinViewModel)
        viewModelOf(::DisablePinViewModel)
        viewModelOf(::ChangePinViewModel)
        viewModelOf(::LockViewModel)
    }
}