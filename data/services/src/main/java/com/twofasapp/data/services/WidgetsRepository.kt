package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.Widgets
import kotlinx.coroutines.flow.Flow

interface WidgetsRepository {
    fun observeWidgets(): Flow<Widgets>
    fun observeWidget(appWidgetId: Int): Flow<Widget>
    suspend fun getWidgets(): Widgets
    suspend fun incrementLastInteraction()
    suspend fun incrementRefreshTicker()
    suspend fun getRefreshTicker(): Long
    suspend fun saveWidget(widget: Widget)
    suspend fun toggleService(appWidgetId: Int, serviceId: Long)
    suspend fun generateHotpCode(serviceId: Long)
    suspend fun hideAll()
    suspend fun deleteWidget(appWidgetIds: List<Int>)
}