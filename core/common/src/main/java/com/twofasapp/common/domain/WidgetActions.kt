package com.twofasapp.common.domain

interface WidgetActions {
    fun onServiceChanged()
    fun onServiceDeleted(serviceId: Long)
}