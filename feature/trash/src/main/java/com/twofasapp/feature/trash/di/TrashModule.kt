package com.twofasapp.feature.trash.di

import com.twofasapp.di.KoinModule
import com.twofasapp.feature.trash.ui.dispose.DisposeViewModel
import com.twofasapp.feature.trash.ui.trash.TrashViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class TrashModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::TrashViewModel)
        viewModelOf(::DisposeViewModel)
    }
}