package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServicesOrder(
    @SerialName("ids")
    val ids: List<Long> = emptyList(),
    @SerialName("type")
    val type: Type = Type.Manual,
) {
    enum class Type {
        Alphabetical,
        Manual,
    }
}