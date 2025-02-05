package com.twofasapp.feature.externalimport.ui.result

import com.twofasapp.common.domain.Service
import com.twofasapp.feature.externalimport.domain.ImportType

internal data class ExternalImportResultUiState(
    val importType: ImportType = ImportType.GoogleAuthenticator,
    val loading: Boolean = true,
    val readResult: ReadResult = ReadResult.Failure(""),
    val finishSuccess: Boolean = false,
)

internal sealed interface ReadResult {
    data class Success(
        val services: List<Service>,
        val countServicesToImport: Int,
        val countTotalServices: Int,
    ) : ReadResult

    data class Failure(val reason: String) : ReadResult
}