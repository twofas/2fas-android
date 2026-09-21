package com.twofasapp.feature.browserext.ui.request

import com.twofasapp.common.domain.Service
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload

internal data class BrowserExtRequestUiState(
    val browserName: String = "",
    val domain: String = "",
    val suggestedServices: List<Service> = emptyList(),
    val otherServices: List<Service> = emptyList(),
    val saveMyChoice: Boolean = false,
    val payload: BrowserExtRequestPayload = BrowserExtRequestPayload(
        action = BrowserExtRequestPayload.Action.Approve,
        notificationId = 0,
        extensionId = "",
        requestId = "",
        serviceId = 0L,
        domain = null,
    ),
)