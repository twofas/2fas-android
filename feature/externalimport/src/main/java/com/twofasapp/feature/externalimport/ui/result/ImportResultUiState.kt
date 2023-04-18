package com.twofasapp.feature.externalimport.ui.result

import com.twofasapp.feature.externalimport.domain.ExternalImport

internal data class ImportResultUiState(
    val importResult: ExternalImport? = null,
    val title: Int? = null,
    val description: Int? = null,
    val servicesToImport: Int = 0,
    val totalServicesCount: Int = 0,
    val footer: Int? = null,
    val button: Int? = null,
    val finishSuccess: Boolean = false,
)