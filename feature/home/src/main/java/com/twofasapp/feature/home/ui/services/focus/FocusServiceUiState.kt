package com.twofasapp.feature.home.ui.services.focus

import com.twofasapp.common.domain.Service

internal data class FocusServiceUiState(
    val service: Service? = null,
    val showNextCode: Boolean = false,
    val hideCodes: Boolean = false,
)
