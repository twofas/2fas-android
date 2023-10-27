package com.twofasapp.feature.browserext.ui.permission

import androidx.lifecycle.ViewModel
import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.MutableStateFlow

internal class BrowserExtPermissionViewModel(
    private val browserExtRepository: BrowserExtRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtPermissionUiState())
}