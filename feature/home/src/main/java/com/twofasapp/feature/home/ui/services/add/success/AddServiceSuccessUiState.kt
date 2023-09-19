package com.twofasapp.feature.home.ui.services.add.success

import com.twofasapp.common.domain.Service

internal data class AddServiceSuccessUiState(
    val service: Service? = null,
    val showNextCode: Boolean = false,
    val hideCodes: Boolean = false,
)
