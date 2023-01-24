package com.twofasapp.feature.externalimport.ui.result

import com.twofasapp.feature.externalimport.domain.ExternalImport

internal data class ImportResultUiState(
    val importResult: ExternalImport? = null,
    val title: String = "",
    val description: String = "",
    val counter: String = "",
    val footer: String = "",
    val button: String = "",
    val finishSuccess: Boolean = false,
)