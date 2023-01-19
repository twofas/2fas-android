package com.twofasapp.security

import com.twofasapp.di.KoinModule
import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.data.SecurityRepositoryImpl
import com.twofasapp.security.domain.*
import com.twofasapp.security.ui.changepin.ChangePinScreenFactory
import com.twofasapp.security.ui.changepin.ChangePinViewModel
import com.twofasapp.security.ui.disablepin.DisablePinScreenFactory
import com.twofasapp.security.ui.disablepin.DisablePinViewModel
import com.twofasapp.security.ui.lock.LockViewModel
import com.twofasapp.security.ui.security.SecurityScreenFactory
import com.twofasapp.security.ui.security.SecurityViewModel
import com.twofasapp.security.ui.setuppin.SetupPinScreenFactory
import com.twofasapp.security.ui.setuppin.SetupPinViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class SecurityModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::SecurityViewModel)
        viewModelOf(::SetupPinViewModel)
        viewModelOf(::DisablePinViewModel)
        viewModelOf(::ChangePinViewModel)
        viewModelOf(::LockViewModel)

        singleOf(::SecurityScreenFactory)
        singleOf(::SetupPinScreenFactory)
        singleOf(::ChangePinScreenFactory)
        singleOf(::DisablePinScreenFactory)

        singleOf(::SecurityRepositoryImpl) { bind<SecurityRepository>() }
        singleOf(::ObservePinOptionsCaseImpl) { bind<ObservePinOptionsCase>() }
        singleOf(::EditPinOptionsCaseImpl) { bind<EditPinOptionsCase>() }
        singleOf(::EditPinCaseImpl) { bind<EditPinCase>() }
        singleOf(::ObserveLockMethodCaseImpl) { bind<ObserveLockMethodCase>() }
        singleOf(::GetLockMethodCaseImpl) { bind<GetLockMethodCase>() }
        singleOf(::EditLockMethodCaseImpl) { bind<EditLockMethodCase>() }
        singleOf(::GetPinCaseImpl) { bind<GetPinCase>() }
        singleOf(::ObserveInvalidPinStatusCaseImpl) { bind<ObserveInvalidPinStatusCase>() }
        singleOf(::EditInvalidPinStatusCaseImpl) { bind<EditInvalidPinStatusCase>() }

    }
}