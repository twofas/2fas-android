package com.twofasapp.data.browserext.di

import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.browserext.BrowserExtRepositoryImpl
import com.twofasapp.data.browserext.local.BrowserExtLocalSource
import com.twofasapp.data.browserext.remote.BrowserExtRemoteSource
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataBrowserExtModule : KoinModule {
    override fun provide() = module {
        singleOf(::BrowserExtLocalSource)
        singleOf(::BrowserExtRemoteSource)
        singleOf(::BrowserExtRepositoryImpl) { bind<BrowserExtRepository>() }
    }
}