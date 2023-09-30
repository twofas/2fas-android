package com.twofasapp.feature.externalimport.ui.main

import com.twofasapp.feature.externalimport.domain.ImportType

internal data class ExternalImportUiState(
    val importType: ImportType = ImportType.GoogleAuthenticator,
)
