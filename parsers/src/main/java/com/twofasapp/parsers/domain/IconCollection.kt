package com.twofasapp.parsers.domain

data class IconCollection(
    val id: String,
    val name: String,
    val icons: List<Icon>,
) {

    data class Icon(
        val id: String,
        val type: IconType,
    )

    enum class IconType {
        Light, Dark
    }
}