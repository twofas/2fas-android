package com.twofasapp.common.domain

interface WidgetCallbacks {
    suspend fun onServiceChanged()
    suspend fun onServiceDeleted(serviceId: Long)
}