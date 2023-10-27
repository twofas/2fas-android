package com.twofasapp.feature.home.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.feature.home.ui.editservice.changebrand.ChangeBrandViewModel
import com.twofasapp.feature.home.ui.notifications.NotificationsViewModel
import com.twofasapp.feature.home.ui.services.ServicesViewModel
import com.twofasapp.feature.home.ui.services.add.manual.AddServiceManualViewModel
import com.twofasapp.feature.home.ui.services.add.scan.AddServiceScanViewModel
import com.twofasapp.feature.home.ui.services.add.success.AddServiceSuccessViewModel
import com.twofasapp.feature.home.ui.services.focus.FocusServiceViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class HomeModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::ServicesViewModel)
        viewModelOf(::NotificationsViewModel)
        viewModelOf(::AddServiceManualViewModel)
        viewModelOf(::AddServiceScanViewModel)
        viewModelOf(::AddServiceSuccessViewModel)
        viewModelOf(::FocusServiceViewModel)
        viewModelOf(::EditServiceViewModel)
        viewModelOf(::ChangeBrandViewModel)
    }
}