package com.twofasapp.feature.home.ui.services.add.scan

import com.twofasapp.data.services.domain.RecentlyAddedService

internal data class AddServiceScanUiState(
    val scanned: String = "",
    val enabled: Boolean = true,
    val showInvalidQrDialog: Boolean = false,
    val showServiceExistsDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showGalleryErrorDialog: Boolean = false,
    val source: AddServiceScanViewModel.Source = AddServiceScanViewModel.Source.Scan,
)

internal sealed interface AddServiceScanUiEvent {
    data class AddedSuccessfully(val recentlyAddedService: RecentlyAddedService) : AddServiceScanUiEvent
}
