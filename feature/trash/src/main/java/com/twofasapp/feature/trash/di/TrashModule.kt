package com.twofasapp.feature.trash.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.trash.ui.trash.TrashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class TrashModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::TrashViewModel)
    }
}