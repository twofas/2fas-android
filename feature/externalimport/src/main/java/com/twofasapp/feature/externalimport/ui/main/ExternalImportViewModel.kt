package com.twofasapp.feature.externalimport.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.feature.externalimport.domain.ImportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExternalImportViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val importType: ImportType = enumValueOf(savedStateHandle.getOrThrow(NavArg.ImportType.name))

    val uiState = MutableStateFlow(ExternalImportUiState())

    init {
        uiState.update { it.copy(importType = importType) }
    }

}