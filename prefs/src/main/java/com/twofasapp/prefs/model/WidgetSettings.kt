package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettings(
    @SerialName("widgets")
    val widgets: List<Widget> = emptyList(),
) {
    fun getWidgetForId(appWidgetId: Int): Widget? =
        widgets.find { it.appWidgetId == appWidgetId }
}
