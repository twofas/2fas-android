package com.twofasapp.widgets.presenter

interface WidgetPresenterDelegate {
    fun updateWidget()
    fun deleteWidget(appWidgetIds: IntArray)
}