package com.twofasapp.feature.externalimport.ui.main

import androidx.lifecycle.ViewModel
import com.twofasapp.feature.externalimport.domain.ImportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExternalImportViewModel(
    private val importType: ImportType,
) : ViewModel() {

    val uiState = MutableStateFlow(ExternalImportUiState())

    init {
        uiState.update { it.copy(importType = importType) }
    }
}