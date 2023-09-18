package com.twofasapp.widgets.presenter

import android.widget.RemoteViews
import com.twofasapp.entity.ServiceModel

interface WidgetItemPresenterDelegate {
    fun updateItemView(appWidgetId: Int, position: Int, widgetService: com.twofasapp.prefs.model.WidgetEntity.Service, model: ServiceModel): RemoteViews
}