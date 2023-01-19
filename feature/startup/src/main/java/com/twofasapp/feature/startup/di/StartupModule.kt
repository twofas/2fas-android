package com.twofasapp.feature.startup.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.startup.ui.StartupViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class StartupModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::StartupViewModel)
    }
}