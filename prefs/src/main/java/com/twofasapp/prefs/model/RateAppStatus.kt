@file:UseSerializers(InstantSerializer::class)

package com.twofasapp.prefs.model

import com.twofasapp.serialization.converter.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class RateAppStatus(
    @SerialName("neverShowAgain")
    val neverShowAgain: Boolean = false,
    @SerialName("counter")
    val counter: Int = 0,
    @SerialName("counterStartedMillis")
    val counterStarted: Instant = Instant.now(),
    @SerialName("counterReachedMillis")
    val counterReached: Instant = Instant.now()
)
