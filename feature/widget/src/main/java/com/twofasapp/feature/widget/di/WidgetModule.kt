package com.twofasapp.feature.widget.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.domain.WidgetCallbacks
import com.twofasapp.feature.widget.sync.WidgetCallbacksImpl
import com.twofasapp.feature.widget.ui.settings.WidgetSettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import javax.inject.Provider

class WidgetModule : KoinModule {
    override fun provide(): Module = module {
        single<WidgetCallbacks> {
            WidgetCallbacksImpl(
                context = androidApplication(),
                widgetsRepository = Provider { get() }
            )
        }

        viewModelOf(::WidgetSettingsViewModel)
    }
}