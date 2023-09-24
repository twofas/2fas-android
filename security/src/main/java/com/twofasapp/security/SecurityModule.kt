package com.twofasapp.security

import com.twofasapp.common.di.KoinModule
import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.data.SecurityRepositoryImpl
import com.twofasapp.security.domain.EditInvalidPinStatusCase
import com.twofasapp.security.domain.EditInvalidPinStatusCaseImpl
import com.twofasapp.security.domain.EditLockMethodCase
import com.twofasapp.security.domain.EditLockMethodCaseImpl
import com.twofasapp.security.domain.EditPinCase
import com.twofasapp.security.domain.EditPinCaseImpl
import com.twofasapp.security.domain.EditPinOptionsCase
import com.twofasapp.security.domain.EditPinOptionsCaseImpl
import com.twofasapp.security.domain.GetLockMethodCase
import com.twofasapp.security.domain.GetLockMethodCaseImpl
import com.twofasapp.security.domain.GetPinCase
import com.twofasapp.security.domain.GetPinCaseImpl
import com.twofasapp.security.domain.ObserveInvalidPinStatusCase
import com.twofasapp.security.domain.ObserveInvalidPinStatusCaseImpl
import com.twofasapp.security.domain.ObserveLockMethodCase
import com.twofasapp.security.domain.ObserveLockMethodCaseImpl
import com.twofasapp.security.domain.ObservePinOptionsCase
import com.twofasapp.security.domain.ObservePinOptionsCaseImpl
import com.twofasapp.security.ui.changepin.ChangePinViewModel
import com.twofasapp.security.ui.disablepin.DisablePinViewModel
import com.twofasapp.security.ui.lock.LockViewModel
import com.twofasapp.security.ui.security.SecurityViewModel
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