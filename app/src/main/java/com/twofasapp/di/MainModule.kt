package com.twofasapp.di

import com.twofasapp.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class MainModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::MainViewModel)
    }
}