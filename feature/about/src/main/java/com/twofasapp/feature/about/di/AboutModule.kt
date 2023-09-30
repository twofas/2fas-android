package com.twofasapp.feature.about.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.about.ui.about.AboutViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class AboutModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::AboutViewModel)
    }
}