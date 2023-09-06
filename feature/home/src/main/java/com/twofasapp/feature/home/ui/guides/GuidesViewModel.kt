package com.twofasapp.feature.home.ui.guides

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class GuidesViewModel : ViewModel() {
    val uiState = MutableStateFlow(GuidesUiState())

}