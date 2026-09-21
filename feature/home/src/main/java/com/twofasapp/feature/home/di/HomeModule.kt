package com.twofasapp.feature.home.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.feature.home.ui.editservice.changebrand.ChangeBrandViewModel
import com.twofasapp.feature.home.ui.notifications.NotificationsViewModel
import com.twofasapp.feature.home.ui.services.HomeViewModel
import com.twofasapp.feature.home.ui.services.add.manual.AddServiceManualViewModel
import com.twofasapp.feature.home.ui.services.add.scan.AddServiceScanViewModel
import com.twofasapp.feature.home.ui.services.add.success.AddServiceSuccessViewModel
import com.twofasapp.feature.home.ui.services.component.AppReviewViewModel
import com.twofasapp.feature.home.ui.services.focus.FocusServiceViewModel
import com.twofasapp.feature.home.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class HomeModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::HomeViewModel)
        viewModelOf(::SettingsViewModel)
        viewModel { AppReviewViewModel(androidContext(), get()) }
        viewModelOf(::NotificationsViewModel)
        viewModelOf(::AddServiceScanViewModel)
        viewModelOf(::AddServiceManualViewModel)
        viewModel { (serviceId: Long) ->
            AddServiceSuccessViewModel(
                serviceId = serviceId,
                servicesRepository = get(),
                customizationRepository = get(),
            )
        }
        viewModel { (serviceId: Long) ->
            FocusServiceViewModel(
                serviceId = serviceId,
                servicesRepository = get(),
                customizationRepository = get(),
            )
        }
        viewModel { (serviceId: Long) ->
            EditServiceViewModel(
                serviceId = serviceId,
                groupsRepository = get(),
                servicesRepository = get(),
                securityRepository = get(),
            )
        }
        viewModelOf(::ChangeBrandViewModel)
    }
}