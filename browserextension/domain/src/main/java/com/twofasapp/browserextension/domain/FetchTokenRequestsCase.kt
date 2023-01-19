package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.TokenRequest

interface FetchTokenRequestsCase {
    suspend operator fun invoke(extensionId: String): List<TokenRequest>
}