package com.twofasapp.feature.home.ui.services.add.scan

import com.twofasapp.data.services.domain.RecentlyAddedService

internal data class AddServiceScanUiState(
    val scanned: String = "",
    val enabled: Boolean = true,
    val showInvalidQrDialog: Boolean = false,
    val showServiceExistsDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showGalleryErrorDialog: Boolean = false,
    val source: AddServiceScanViewModel.ScanSource = AddServiceScanViewModel.ScanSource.Scan,
    val addedService: RecentlyAddedService? = null,
)