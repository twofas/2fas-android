package com.twofasapp.data.session.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PinOptionsEntity(
    @SerialName("digits")
    val digits: Int,
    @SerialName("trials")
    val trials: Int,
    @SerialName("timeout")
    val timeout: Int,
)