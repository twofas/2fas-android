package com.twofasapp.data.services.domain

data class ServicesOrder(
    val ids: List<Long> = emptyList(),
    val type: Type = Type.Manual,
) {
    enum class Type {
        Alphabetical,
        Manual,
    }
}