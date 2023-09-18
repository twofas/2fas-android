package com.twofasapp.data.services.domain

data class Widget(
    val appWidgetId: Int,
    val lastInteraction: Long,
    val services: List<WidgetService>,
)
