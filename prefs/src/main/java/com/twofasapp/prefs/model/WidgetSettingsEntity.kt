package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettingsEntity(
    @SerialName("widgets")
    val widgets: List<WidgetEntity> = emptyList(),
) {
    fun getWidgetForId(appWidgetId: Int): WidgetEntity? =
        widgets.find { it.appWidgetId == appWidgetId }
}
