package com.twofasapp.feature.home.ui.editservice

import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.domain.Group

internal data class EditServiceUiState(
    val service: Service = Service.Empty,
    val persistedService: Service = Service.Empty,
    val finish: Boolean = false,
    val hasLock: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isSecretVisible: Boolean = false,
    val isQrVisible: Boolean = false,
    val hasChanges: Boolean = false,
    val groups: List<Group> = emptyList(),
    val isInputNameValid: Boolean = true,
    val isInputInfoValid: Boolean = true,
)

internal sealed interface EditServiceUiEvent {
    data object Finish : EditServiceUiEvent
}