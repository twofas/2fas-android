package com.twofasapp.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.twofasapp.feature.widget.GlanceWidget
import org.koin.core.component.KoinComponent
import timber.log.Timber

/**
 * Never rename/move this class as it will cause widget to be removed.
 * User would have to set it up again.
 */
class WidgetProvider : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceWidget = GlanceWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Timber.d("onUpdate")
        update(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Timber.d("onEnabled")
        update(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Timber.d("onDeleted: $appWidgetIds")
        glanceAppWidget.deleteWidget(context, appWidgetIds.toList())
    }

    private fun update(context: Context) {
        glanceAppWidget.runUpdateWork(context)
    }
}