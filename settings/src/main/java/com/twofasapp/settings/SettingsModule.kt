package com.twofasapp.settings

import com.twofasapp.di.KoinModule
import com.twofasapp.settings.ui.main.SettingsMainScreenFactory
import com.twofasapp.settings.ui.main.SettingsMainViewModel
import com.twofasapp.settings.ui.theme.ThemeScreenFactory
import com.twofasapp.settings.ui.theme.ThemeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class SettingsModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::SettingsMainViewModel)
        viewModelOf(::ThemeViewModel)

        singleOf(::SettingsMainScreenFactory)
        singleOf(::ThemeScreenFactory)
    }
}