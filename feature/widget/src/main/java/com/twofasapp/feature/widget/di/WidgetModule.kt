package com.twofasapp.feature.widget.di

import com.twofasapp.common.domain.WidgetCallbacks
import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.widget.sync.WidgetCallbacksImpl
import com.twofasapp.feature.widget.ui.settings.WidgetSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class WidgetModule : KoinModule {
    override fun provide(): Module = module {
        singleOf(::WidgetCallbacksImpl) { bind<WidgetCallbacks>() }

        viewModelOf(::WidgetSettingsViewModel)
    }
}