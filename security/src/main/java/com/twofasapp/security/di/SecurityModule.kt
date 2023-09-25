package com.twofasapp.security.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.security.ui.changepin.ChangePinViewModel
import com.twofasapp.security.ui.disablepin.DisablePinViewModel
import com.twofasapp.security.ui.lock.LockViewModel
import com.twofasapp.security.ui.security.SecurityViewModel
import com.twofasapp.security.ui.setuppin.SetupPinViewModel
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