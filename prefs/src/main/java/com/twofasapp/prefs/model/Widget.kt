package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Widget(
    @SerialName("appWidgetId")
    val appWidgetId: Int,
    @SerialName("lastInteractionTimestamp")
    val lastInteractionTimestamp: Long,
    @SerialName("services")
    val services: MutableList<Service> = mutableListOf(),
) : Serializable {

    @kotlinx.serialization.Serializable
    data class Service(
        @SerialName("id")
        val id: Long,
        @SerialName("isActive")
        val isActive: Boolean,
    ) : Serializable
}