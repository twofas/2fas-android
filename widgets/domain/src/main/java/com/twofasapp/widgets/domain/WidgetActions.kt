package com.twofasapp.widgets.domain

interface WidgetActions {
    fun onServiceChanged()
    fun onServiceDeleted(serviceId: Long)
}