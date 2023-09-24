package com.twofasapp.feature.externalimport.ui.scan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class ExternalImportScanViewModel : ViewModel() {
    val uiState = MutableStateFlow(ExternalImportScanUiState())
}