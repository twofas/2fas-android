package com.twofasapp.feature.widget.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.widget.ui.configure.WidgetSetupActivity
import com.twofasapp.feature.widget.ui.configure.WidgetSetupViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

class WidgetModule : KoinModule {
    override fun provide(): Module = module {
        viewModelOf(::WidgetSetupViewModel)
    }
}