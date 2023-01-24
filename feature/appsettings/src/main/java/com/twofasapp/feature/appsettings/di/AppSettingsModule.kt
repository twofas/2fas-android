package com.twofasapp.feature.appsettings.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.appsettings.ui.AppSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class AppSettingsModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::AppSettingsViewModel)
    }
}