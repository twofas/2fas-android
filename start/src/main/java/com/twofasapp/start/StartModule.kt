package com.twofasapp.start

import com.twofasapp.di.KoinModule
import com.twofasapp.start.domain.ClearObsoletePrefsCase
import com.twofasapp.start.domain.ClearObsoletePrefsCaseImpl
import com.twofasapp.start.domain.DeeplinkHandler
import com.twofasapp.start.domain.EditShowOnboardingCase
import com.twofasapp.start.domain.EditShowOnboardingCaseImpl
import com.twofasapp.start.domain.GetShowOnboardingCase
import com.twofasapp.start.domain.GetShowOnboardingCaseImpl
import com.twofasapp.start.domain.MigratePinCase
import com.twofasapp.start.domain.MigratePinCaseImpl
import com.twofasapp.start.domain.MigrateUnknownServicesCase
import com.twofasapp.start.domain.MigrateUnknownServicesCaseImpl
import com.twofasapp.start.domain.work.OnAppUpdatedWorkDispatcher
import com.twofasapp.start.ui.onboarding.OnboardingViewModel
import com.twofasapp.start.work.OnAppUpdatedWorkDispatcherImpl
import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.PlainPreferences
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class StartModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::OnboardingViewModel)

        singleOf(::DeeplinkHandler)

        singleOf(::OnAppUpdatedWorkDispatcherImpl) { bind<OnAppUpdatedWorkDispatcher>() }

        single<ClearObsoletePrefsCase> {
            ClearObsoletePrefsCaseImpl(
                get<PlainPreferences>(),
                get<EncryptedPreferences>(),
            )
        }
        singleOf(::GetShowOnboardingCaseImpl) { bind<GetShowOnboardingCase>() }
        singleOf(::EditShowOnboardingCaseImpl) { bind<EditShowOnboardingCase>() }
        singleOf(::MigratePinCaseImpl) { bind<MigratePinCase>() }
        singleOf(::MigrateUnknownServicesCaseImpl) { bind<MigrateUnknownServicesCase>() }
    }
}