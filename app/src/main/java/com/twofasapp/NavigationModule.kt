package com.twofasapp

import com.twofasapp.di.KoinModule
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.navigation.SecurityRouterImpl
import com.twofasapp.navigation.ServiceExternalNavigatorImpl
import com.twofasapp.navigation.StartRouter
import com.twofasapp.navigation.StartRouterImpl
import com.twofasapp.services.ui.ServiceExternalNavigator
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class NavigationModule : KoinModule {

    override fun provide() = module {
        factoryOf(::StartRouterImpl) { bind<StartRouter>() }
        singleOf(::SecurityRouterImpl) { bind<SecurityRouter>() }

        singleOf(::ServiceExternalNavigatorImpl) { bind<ServiceExternalNavigator>() }
    }
}