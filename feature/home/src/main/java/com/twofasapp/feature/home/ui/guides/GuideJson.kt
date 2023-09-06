package com.twofasapp.feature.home.ui.guides

import kotlinx.serialization.Serializable

@Serializable
data class GuideJson(
    val serviceName: String,
    val serviceId: String,
    val flow: Flow,
) {
    @Serializable
    data class Flow(
        val header: String,
        val menu: Menu,
    )

    @Serializable
    data class Menu(
        val title: String,
        val items: List<Variant>,
    )

    @Serializable
    data class Variant(
        val name: String,
        val steps: List<Step>,
    )

    @Serializable
    data class Step(
        val image: String,
        val content: String,
        val cta: Cta?,
    )

    @Serializable
    data class Cta(
        val name: String,
        val action: String,
        val data: String?,
    )
}