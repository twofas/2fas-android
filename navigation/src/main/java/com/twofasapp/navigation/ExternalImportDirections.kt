package com.twofasapp.navigation

import com.twofasapp.navigation.base.Directions

sealed interface ExternalImportDirections : Directions {
    object GoBack : ExternalImportDirections
    object Main : ExternalImportDirections
    data class ImportScan(val startWithGallery: Boolean = false) : ExternalImportDirections
    data class ImportResult(val type: Type, val content: String) : ExternalImportDirections {
        enum class Type { GoogleAuthenticator, Aegis, Raivo }
    }

    object GoogleAuthenticator : ExternalImportDirections
    object Aegis : ExternalImportDirections
    object Raivo : ExternalImportDirections
}