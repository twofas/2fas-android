package com.twofasapp.feature.trash.ui.trash

import com.twofasapp.common.domain.Service

internal data class TrashUiState(
    val trashedItems: List<Service> = emptyList(),
    val selected: List<Long> = emptyList(),
) {
    val hasSelections: Boolean
        get() = selected.isNotEmpty()
}