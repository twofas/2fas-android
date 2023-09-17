package com.twofasapp.di

import com.twofasapp.DeeplinkHandler
import com.twofasapp.storage.EncryptedPreferences
import com.twofasapp.storage.PlainPreferences
import com.twofasapp.usecases.ClearObsoletePrefsCase
import com.twofasapp.usecases.ClearObsoletePrefsCaseImpl
import com.twofasapp.usecases.EditShowOnboardingCase
import com.twofasapp.usecases.EditShowOnboardingCaseImpl
import com.twofasapp.usecases.GetShowOnboardingCase
import com.twofasapp.usecases.GetShowOnboardingCaseImpl
import com.twofasapp.usecases.MigratePinCase
import com.twofasapp.usecases.MigratePinCaseImpl
import com.twofasapp.usecases.MigrateUnknownServicesCase
import com.twofasapp.usecases.MigrateUnknownServicesCaseImpl
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcherImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class StartModule : KoinModule {

    override fun provide() = module {
        singleOf(::DeeplinkHandler)

        singleOf(::OnAppUpdatedWorkDispatcherImpl) { bind<com.twofasapp.workmanager.OnAppUpdatedWorkDispatcher>() }

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