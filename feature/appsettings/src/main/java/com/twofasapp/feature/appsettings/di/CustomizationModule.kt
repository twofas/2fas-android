package com.twofasapp.feature.appsettings.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.appsettings.ui.CustomizationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class CustomizationModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::CustomizationViewModel)
    }
}