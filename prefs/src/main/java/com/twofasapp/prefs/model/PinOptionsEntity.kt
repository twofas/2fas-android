package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinOptionsEntity(
    @SerialName("digits")
    val digits: Int,
    @SerialName("trials")
    val trials: Int,
    @SerialName("timeout")
    val timeout: Int,
)
