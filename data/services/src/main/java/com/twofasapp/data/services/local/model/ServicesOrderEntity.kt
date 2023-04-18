package com.twofasapp.data.services.local.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ServicesOrderEntity(
    val ids: List<Long> = emptyList(),
    val type: Type = Type.Manual,
) {
    enum class Type {
        Alphabetical,
        Manual,
    }
}