package com.twofasapp.feature.externalimport.ui.main

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.qrscan.ReadQrFromImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExternalImportViewModel(
    private val importType: ImportType,
    private val readQrFromImage: ReadQrFromImage,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
) : ViewModel() {

    val uiState = MutableStateFlow(ExternalImportUiState())

    init {
        uiState.update { it.copy(importType = importType) }
    }

    fun onImagePicked(uri: String) {
        launchScoped {
            val content = readQrFromImage.invoke(uri.toUri()).getOrNull()
            val batch = content?.let { googleAuthenticatorImporter.readBatch(it) }

            uiState.update {
                it.copy(
                    pickedImage = if (content != null && batch?.isMultiCode == true) {
                        PickedImage.Batch(content)
                    } else {
                        PickedImage.Single(uri)
                    },
                )
            }
        }
    }

    fun consumePickedImage() {
        uiState.update { it.copy(pickedImage = null) }
    }
}