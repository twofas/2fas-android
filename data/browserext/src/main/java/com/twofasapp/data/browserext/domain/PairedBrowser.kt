package com.twofasapp.data.browserext.domain

import java.time.Instant

data class PairedBrowser(
    val id: String,
    val name: String,
    val pairedAt: Instant,
    val extensionPublicKey: String,
)