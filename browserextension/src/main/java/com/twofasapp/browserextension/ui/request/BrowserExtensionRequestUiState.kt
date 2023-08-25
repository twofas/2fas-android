package com.twofasapp.browserextension.ui.request

import com.twofasapp.services.domain.model.Service

data class BrowserExtensionRequestUiState(
    val browserName: String = "",
    val suggestedServices: List<Service> = emptyList(),
    val otherServices: List<Service> = emptyList(),
    val saveMyChoice: Boolean = false,
)