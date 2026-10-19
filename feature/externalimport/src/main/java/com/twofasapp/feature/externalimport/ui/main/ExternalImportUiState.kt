package com.twofasapp.feature.externalimport.ui.main

import com.twofasapp.feature.externalimport.domain.ImportType

internal data class ExternalImportUiState(
    val importType: ImportType = ImportType.GoogleAuthenticator,
    val pickedImage: PickedImage? = null,
)

internal sealed interface PickedImage {
    data class Single(val uri: String) : PickedImage
    data class Batch(val content: String) : PickedImage
}