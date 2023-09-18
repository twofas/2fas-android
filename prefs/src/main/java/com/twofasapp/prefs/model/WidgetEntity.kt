package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WidgetEntity(
    @SerialName("appWidgetId")
    val appWidgetId: Int,
    @SerialName("lastInteractionTimestamp")
    val lastInteractionTimestamp: Long,
    @SerialName("services")
    val services: MutableList<Service> = mutableListOf(),
) {

    @Serializable
    data class Service(
        @SerialName("id")
        val id: Long,
        @SerialName("isActive")
        val isActive: Boolean,
    )
}