package com.twofasapp.externalimport

import com.twofasapp.di.KoinModule
import com.twofasapp.externalimport.domain.AegisImporter
import com.twofasapp.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.externalimport.domain.RaivoImporter
import com.twofasapp.externalimport.ui.aegis.AegisScreenFactory
import com.twofasapp.externalimport.ui.googleauthenticator.GoogleAuthenticatorScreenFactory
import com.twofasapp.externalimport.ui.main.ExternalImportScreenFactory
import com.twofasapp.externalimport.ui.raivo.RaivoScreenFactory
import com.twofasapp.externalimport.ui.result.ImportResultScreenFactory
import com.twofasapp.externalimport.ui.result.ImportResultViewModel
import com.twofasapp.externalimport.ui.scan.ImportScanScreenFactory
import com.twofasapp.externalimport.ui.scan.ImportScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class ExternalImportModule : KoinModule {

    override fun provide() = module {
        viewModelOf(::ImportScanViewModel)
        viewModelOf(::ImportResultViewModel)

        factoryOf(::GoogleAuthenticatorImporter)
        factoryOf(::AegisImporter)
        factoryOf(::RaivoImporter)

        singleOf(::ExternalImportScreenFactory)
        singleOf(::GoogleAuthenticatorScreenFactory)
        singleOf(::AegisScreenFactory)
        singleOf(::RaivoScreenFactory)
        singleOf(::ImportScanScreenFactory)
        singleOf(::ImportResultScreenFactory)
    }
}