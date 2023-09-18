package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Widgets

interface WidgetsRepository {
    suspend fun getWidgets(): Widgets
    suspend fun toggleService(serviceId: Long)
}