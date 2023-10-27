package com.twofasapp.common.di

import com.twofasapp.common.coroutines.AppDispatchers
import com.twofasapp.common.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class CommonModule : KoinModule {

    override fun provide(): Module = module {
        singleOf(::AppDispatchers) { bind<Dispatchers>() }
    }
}