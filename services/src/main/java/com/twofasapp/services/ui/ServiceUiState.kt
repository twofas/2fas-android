package com.twofasapp.services.ui

import com.twofasapp.prefs.model.Groups
import com.twofasapp.services.domain.model.Service

internal data class ServiceUiState(
    val service: Service = Service.createDefault(),
    val persistedService: Service = Service.createDefault(),
    val showServiceExistsDialog: Boolean = false,
    val showInsertErrorDialog: Boolean = false,
    val finish: Boolean = false,
    val finishWithResult: Boolean = false,
    val hasLock: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isSecretVisible: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val hasChanges: Boolean = false,
    val groups: Groups = Groups(),
    val isInputNameValid: Boolean = service.id != 0L,
    val isInputSecretValid: Boolean = service.id != 0L,
    val isInputInfoValid: Boolean = true,
    val hasSavedLabel: Boolean = false,
)