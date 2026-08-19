package com.twofasapp.feature.startup.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.startup.ui.startup.StartupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class StartupModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::StartupViewModel)
    }
}