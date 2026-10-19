package com.twofasapp.feature.externalimport.ui.scan

internal data class ExternalImportScanUiState(
    val fromGallery: Boolean = false,
    val scanning: Boolean = true,
    val batch: BatchState? = null,
    val scanError: ScanError? = null,
    val importContent: String? = null,
)

internal data class BatchState(
    val size: Int,
    val id: Int,
    val scanned: Map<Int, ScannedCode> = emptyMap(),
) {
    val isComplete: Boolean
        get() = scanned.size == size
}

internal data class ScannedCode(
    val content: String,
    val servicesCount: Int,
)

internal enum class ScanError {
    AlreadyScanned,
    DifferentExport,
    ReadError,
}