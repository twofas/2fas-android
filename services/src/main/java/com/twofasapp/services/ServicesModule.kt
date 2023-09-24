package com.twofasapp.services

import com.twofasapp.common.di.KoinModule
import com.twofasapp.services.ui.EditServiceViewModel
import com.twofasapp.services.ui.changebrand.ChangeBrandViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

class ServicesModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::EditServiceViewModel)
        viewModelOf(::ChangeBrandViewModel)
    }
}