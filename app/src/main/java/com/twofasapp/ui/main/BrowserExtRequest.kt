package com.twofasapp.ui.main

import com.twofasapp.common.domain.Service
import com.twofasapp.data.browserext.domain.TokenRequest

data class BrowserExtRequest(
    val request: TokenRequest,
    val domain: String,
    val matchedServices: List<Service>,
)