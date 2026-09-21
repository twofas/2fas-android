package com.twofasapp.feature.about.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.about.ui.about.AboutViewModel
import com.twofasapp.feature.about.ui.licenses.OpenSourceLibrariesProvider
import com.twofasapp.feature.about.ui.licenses.OpenSourceLibrariesProviderImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class AboutModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::AboutViewModel)
        singleOf(::OpenSourceLibrariesProviderImpl) { bind<OpenSourceLibrariesProvider>() }
    }
}