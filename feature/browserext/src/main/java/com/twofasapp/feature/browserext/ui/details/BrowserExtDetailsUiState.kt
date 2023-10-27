package com.twofasapp.feature.browserext.ui.details

import java.time.Instant

internal data class BrowserExtDetailsUiState(
    val browserName: String = "",
    val browserPairedAt: Instant = Instant.now(),
    val finish: Boolean = false,
)