package com.twofasapp.data.services.domain

data class Widget(
    val appWidgetId: Int,
    val lastInteraction: Long = 0L,
    val services: List<WidgetService> = emptyList(),
) {
    val selectedServices: List<Long>
        get() = services.map { it.service.id }
}
