package com.twofasapp.browserextension.domain.model

import com.twofasapp.time.domain.formatter.TimeFormatter
import java.time.Instant
import java.time.ZoneOffset

data class PairedBrowser(
    val id: String,
    val name: String,
    val pairedAt: Instant,
    val extensionPublicKey: String,
) {
    fun formatPairedAt(): String {
        return pairedAt.atOffset(ZoneOffset.UTC).format(TimeFormatter.fullDate)
    }
}
