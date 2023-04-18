package com.twofasapp.feature.externalimport.domain

import com.twofasapp.prefs.model.ServiceDto

sealed class ExternalImport {
    data class Success(
        val servicesToImport: List<ServiceDto>,
        val totalServicesCount: Int,
    ) : ExternalImport()

    data class ParsingError(
        val reason: Exception
    ) : ExternalImport()

    object UnsupportedError : ExternalImport()
}