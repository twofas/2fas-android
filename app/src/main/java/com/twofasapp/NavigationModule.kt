package com.twofasapp

import com.twofasapp.di.KoinModule
import com.twofasapp.navigation.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class NavigationModule : KoinModule {

    override fun provide() = module {
        factoryOf(::StartRouterImpl) { bind<StartRouter>() }
        singleOf(::SettingsRouterImpl) { bind<SettingsRouter>() }
        singleOf(::ServiceRouterImpl) { bind<ServiceRouter>() }
        singleOf(::SecurityRouterImpl) { bind<SecurityRouter>() }
        singleOf(::ExternalImportRouterImpl) { bind<ExternalImportRouter>() }
    }
}