package com.twofasapp.feature.externalimport.ui.scan

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.decodeBase64ToString
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.logger.Flog
import com.twofasapp.feature.externalimport.domain.GoogleAuthenticatorImporter
import com.twofasapp.feature.qrscan.ReadQrFromImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExternalImportScanViewModel(
    initialContent: String?,
    fromGallery: Boolean,
    private val readQrFromImage: ReadQrFromImage,
    private val googleAuthenticatorImporter: GoogleAuthenticatorImporter,
) : ViewModel() {
    val uiState = MutableStateFlow(ExternalImportScanUiState())

    init {
        uiState.update { it.copy(fromGallery = fromGallery, scanning = fromGallery.not()) }

        // Opened with the first code of a batch already read (gallery or add service scanner)
        if (initialContent != null) {
            uiState.update { it.copy(scanning = false) }
            onContent(initialContent.decodeBase64ToString())
        }
    }

    fun onScanned(content: String) {
        // Camera keeps emitting the same code until the scanner is hidden
        if (uiState.value.scanning.not()) return

        onContent(content)
    }

    fun onImagePicked(uri: String) {
        launchScoped {
            readQrFromImage.invoke(uri.toUri())
                .onSuccess { content -> onContent(content) }
                .onFailure { uiState.update { it.copy(scanError = ScanError.ReadError) } }
        }
    }

    private fun onContent(content: String) {
        val batch = uiState.value.batch
        val scannedBatch = googleAuthenticatorImporter.readBatch(content)

        if (batch == null) {
            if (scannedBatch == null || scannedBatch.isMultiCode.not()) {
                // Single code export (or unparseable content) - let the result screen handle it
                uiState.update { it.copy(scanning = false, importContent = content) }
                return
            }

            Flog.persist(tag = "ExternalImport", message = "Google Authenticator batch detected: size=${scannedBatch.size}")

            uiState.update {
                it.copy(
                    scanning = false,
                    batch = BatchState(
                        size = scannedBatch.size,
                        id = scannedBatch.id,
                        scanned = mapOf(scannedBatch.index to ScannedCode(content, scannedBatch.servicesCount)),
                    ),
                )
            }
            return
        }

        // batch_id is optional (Google Authenticator often leaves it empty), compare it only when both are set
        val isSameExport = scannedBatch != null &&
            scannedBatch.size == batch.size &&
            scannedBatch.index in 0 until batch.size &&
            (batch.id == 0 || scannedBatch.id == 0 || batch.id == scannedBatch.id)

        when {
            isSameExport.not() -> uiState.update { it.copy(scanning = false, scanError = ScanError.DifferentExport) }
            batch.scanned.containsKey(scannedBatch!!.index) -> uiState.update { it.copy(scanning = false, scanError = ScanError.AlreadyScanned) }
            else -> {
                val updatedBatch = batch.copy(
                    scanned = batch.scanned + (scannedBatch.index to ScannedCode(content, scannedBatch.servicesCount)),
                )

                uiState.update {
                    it.copy(
                        scanning = false,
                        batch = updatedBatch,
                        // Open the result screen right away once the last code is scanned
                        importContent = if (updatedBatch.isComplete) updatedBatch.joinedContent() else null,
                    )
                }
            }
        }
    }

    fun startScanning() {
        uiState.update { it.copy(scanning = true) }
    }

    fun stopScanning() {
        uiState.update { it.copy(scanning = false) }
    }

    fun consumeScanError() {
        uiState.update { it.copy(scanError = null) }
    }

    fun importBatch() {
        val batch = uiState.value.batch ?: return

        uiState.update { it.copy(importContent = batch.joinedContent()) }
    }

    fun consumeImportContent() {
        uiState.update {
            it.copy(
                importContent = null,
                // Single code flow navigates away right after scanning, re-enable scanner when user comes back
                scanning = it.batch == null,
            )
        }
    }

    private fun BatchState.joinedContent(): String {
        return scanned.toSortedMap().values.joinToString("\n") { it.content }
    }
}