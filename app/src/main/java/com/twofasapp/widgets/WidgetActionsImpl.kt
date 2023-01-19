package com.twofasapp.widgets

import com.twofasapp.usecases.widgets.DeleteServiceFromWidget
import com.twofasapp.widgets.adapter.WidgetViewsData
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import com.twofasapp.widgets.domain.WidgetActions

class WidgetActionsImpl(
    private val widgetBroadcaster: WidgetBroadcaster,
    private val widgetViewsData: WidgetViewsData,
    private val deleteServiceFromWidget: DeleteServiceFromWidget,
) : WidgetActions {

    override fun onServiceChanged() {
        widgetBroadcaster.sendServiceChanged()
    }

    override fun onServiceDeleted(serviceId: Long) {
        deleteServiceFromWidget.execute(serviceId)
        widgetViewsData.invalidateCache()
        widgetBroadcaster.sendServiceChanged()
    }
}