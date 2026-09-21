package com.twofasapp.feature.externalimport.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.externalimport.domain.AegisImporter
import com.twofasapp.feature.externalimport.domain.AndOtpImporter
import com.twofasapp.feature.externalimport.domain.AuthenticatorProImporter
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.domain.LastPassImporter
import com.twofasapp.feature.externalimport.domain.RaivoImporter
import com.twofasapp.feature.externalimport.ui.main.ExternalImportViewModel
import com.twofasapp.feature.externalimport.ui.result.ExternalImportResultViewModel
import com.twofasapp.feature.externalimport.ui.scan.ExternalImportScanViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class ExternalImportModule : KoinModule {

    override fun provide() = module {
        viewModel { (importType: ImportType, importFileUri: String?, importFileContent: String?) ->
            ExternalImportResultViewModel(
                importType = importType,
                importFileUri = importFileUri,
                importFileContent = importFileContent,
                servicesRepository = get(),
                readQrFromImage = get(),
                googleAuthenticatorImporter = get(),
                aegisImporter = get(),
                raivoImporter = get(),
                lastPassImporter = get(),
                authenticatorProImporter = get(),
                andOtpImporter = get(),
            )
        }
        viewModelOf(::ExternalImportScanViewModel)
        viewModel { (importType: ImportType) -> ExternalImportViewModel(importType = importType) }

        factoryOf(::GoogleAuthenticatorImporter)
        factoryOf(::AegisImporter)
        factoryOf(::RaivoImporter)
        factoryOf(::LastPassImporter)
        factoryOf(::AuthenticatorProImporter)
        factoryOf(::AndOtpImporter)
    }
}