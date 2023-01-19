package com.twofasapp.widgets.adapter

import com.twofasapp.entity.ServiceModel
import com.twofasapp.prefs.model.Widget

interface WidgetViewsData {
    fun getWidgetServices(appWidgetId: Int): List<com.twofasapp.prefs.model.Widget.Service>
    fun getServiceModel(appWidgetId: Int, position: Int): ServiceModel?
    fun invalidateCache()
}