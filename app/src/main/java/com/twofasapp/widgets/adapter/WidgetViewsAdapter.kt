package com.twofasapp.widgets.adapter

import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.twofasapp.widgets.WidgetLogger
import com.twofasapp.widgets.presenter.WidgetPresenter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetViewsAdapter(
    private val appWidgetId: Int
) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val widgetPresenter: WidgetPresenter by inject()
    private val widgetViewsData: WidgetViewsData by inject()

    override fun onDataSetChanged() {
        WidgetLogger.log("Adapter.DataSetChanged ($appWidgetId)")
        widgetPresenter.onDataSetChanged()
    }

    override fun getViewAt(position: Int): RemoteViews? {
        return widgetViewsData.getServiceModel(appWidgetId, position)?.let {
            widgetPresenter.updateItemView(
                appWidgetId = appWidgetId,
                position = position,
                widgetService = widgetViewsData.getWidgetServices(appWidgetId)[position],
                model = it,
            )
        }
    }

    override fun onCreate() = WidgetLogger.log("Adapter.Create ($appWidgetId)")
    override fun onDestroy() = WidgetLogger.log("Adapter.Destroy ($appWidgetId)")
    override fun getCount(): Int = widgetViewsData.getWidgetServices(appWidgetId).size
    override fun getLoadingView(): RemoteViews? = if (widgetViewsData.getWidgetServices(appWidgetId).isNotEmpty()) getViewAt(0) else null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = widgetViewsData.getWidgetServices(appWidgetId)[position].id
    override fun hasStableIds(): Boolean = true
}