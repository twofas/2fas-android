package com.twofasapp.common.domain

interface WidgetCallbacks {
    fun onServiceChanged()
    fun onServiceDeleted(serviceId: Long)
}