package com.twofasapp.widgets

import com.twofasapp.common.domain.WidgetActions

class WidgetActionsImpl(
) : WidgetActions {

    //    override fun onServiceChanged() {
//        widgetBroadcaster.sendServiceChanged()
//    }
//
//    override fun onServiceDeleted(serviceId: Long) {
//        deleteServiceFromWidget.execute(serviceId)
//        widgetViewsData.invalidateCache()
//        widgetBroadcaster.sendServiceChanged()
//    }
    override fun onServiceChanged() {
    }

    override fun onServiceDeleted(serviceId: Long) {
    }
}