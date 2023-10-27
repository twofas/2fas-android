package com.twofasapp.feature.widget.ui.settings

import com.twofasapp.common.domain.Service

data class WidgetSettingsUiState(
    val loading: Boolean = true,
    val services: List<Service> = emptyList(),
    val selected: List<Long> = emptyList(),
)