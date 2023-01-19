package com.twofasapp.widgets.presenter

import android.widget.RemoteViews
import com.twofasapp.entity.ServiceModel
import com.twofasapp.prefs.model.Widget

interface WidgetItemPresenterDelegate {
    fun updateItemView(appWidgetId: Int, position: Int, widgetService: com.twofasapp.prefs.model.Widget.Service, model: ServiceModel): RemoteViews
}