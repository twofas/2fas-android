package com.twofasapp.feature.externalimport.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.externalimport.domain.AegisImporter
import com.twofasapp.feature.externalimport.domain.AuthenticatorProImporter
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.LastPassImporter
import com.twofasapp.feature.externalimport.domain.RaivoImporter
import com.twofasapp.feature.externalimport.ui.result.ImportResultViewModel
import com.twofasapp.feature.externalimport.ui.scan.ImportScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class ExternalImportModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::ImportScanViewModel)
        viewModelOf(::ImportResultViewModel)

        factoryOf(::GoogleAuthenticatorImporter)
        factoryOf(::AegisImporter)
        factoryOf(::RaivoImporter)
        factoryOf(::LastPassImporter)
        factoryOf(::AuthenticatorProImporter)
    }
}