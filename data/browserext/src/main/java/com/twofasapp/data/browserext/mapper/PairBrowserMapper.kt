package com.twofasapp.data.browserext.mapper

import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.data.browserext.remote.model.BrowserJson
import com.twofasapp.data.browserext.remote.model.TokenRequestJson
import java.time.Instant

internal fun TokenRequestJson.asDomain(): TokenRequest {
    return TokenRequest(
        domain = domain,
        requestId = token_request_id,
        extensionId = extension_id
    )
}

internal fun BrowserJson.asDomain(): PairedBrowser {
    return PairedBrowser(
        id = id,
        name = name,
        pairedAt = Instant.parse(paired_at),
        extensionPublicKey = "",
    )
}