package com.twofasapp.ui.main

import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.data.services.domain.Service

data class BrowserExtRequest(
    val request: TokenRequest,
    val domain: String,
    val matchedServices: List<Service>,
)
