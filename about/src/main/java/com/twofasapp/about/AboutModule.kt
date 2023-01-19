package com.twofasapp.about

import com.twofasapp.about.ui.AboutViewModel
import com.twofasapp.di.KoinModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class AboutModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::AboutViewModel)
    }
}