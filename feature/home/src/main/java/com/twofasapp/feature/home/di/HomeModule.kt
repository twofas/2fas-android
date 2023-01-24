package com.twofasapp.feature.home.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.home.ui.bottombar.BottomBarViewModel
import com.twofasapp.feature.home.ui.notifications.NotificationsViewModel
import com.twofasapp.feature.home.ui.services.ServicesViewModel
import com.twofasapp.feature.home.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class HomeModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::BottomBarViewModel)

        viewModelOf(::ServicesViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::NotificationsViewModel)
    }
}