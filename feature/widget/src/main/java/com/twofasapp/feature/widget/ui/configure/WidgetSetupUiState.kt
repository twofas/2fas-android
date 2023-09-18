package com.twofasapp.feature.widget.ui.configure

import com.twofasapp.common.domain.Service

data class WidgetSetupUiState(
    val services: List<Service> = emptyList(),
    val selected: List<Long> = emptyList(),
    val finishSuccess: Boolean = false,
)