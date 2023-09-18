package com.twofasapp.feature.home.ui.services.add.manual

import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.common.domain.Service

internal data class AddServiceManualUiState(
    val serviceName: String? = null,
    val serviceNameError: Int? = null,
    val serviceNameValid: Boolean = false,

    val serviceSecret: String? = null,
    val serviceSecretError: Int? = null,
    val serviceSecretValid: Boolean = false,

    val additionalInfo: String = "",

    val authType: Service.AuthType = Service.AuthType.TOTP,
    val algorithm: Service.Algorithm = Service.Algorithm.SHA1,
    val hotpCounter: Int = 1,
    val refreshTime: Int = 30,
    val digits: Int = 6,

    val iconLight: String? = null,
    val iconDark: String? = null,
    val brand: AddServiceManualViewModel.BrandIcon? = null,

    val advancedExpanded: Boolean = false
) {
    val isFormValid: Boolean
        get() = serviceNameValid && serviceSecretValid
}


internal sealed interface AddServiceManualUiEvent {
    data class AddedSuccessfully(val recentlyAddedService: RecentlyAddedService) : AddServiceManualUiEvent
}
