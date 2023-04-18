package com.twofasapp.widgets.presenter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.twofasapp.ui.main.MainActivity
import com.twofasapp.usecases.widgets.DeleteWidget
import com.twofasapp.widgets.R
import com.twofasapp.widgets.WidgetProvider
import com.twofasapp.widgets.adapter.WidgetViewsService
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import com.twofasapp.widgets.configure.WidgetSettingsActivity

class WidgetPresenterDelegateImpl(
    private val context: Context,
    private val deleteWidget: DeleteWidget,
) : WidgetPresenterDelegate {

    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(context)

    private val appWidgetIds: IntArray
        get() = appWidgetManager.getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))

    override fun updateWidget() {
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget)

        appWidgetIds.forEach { appWidgetId ->
            remoteViews.setLogoButton(appWidgetId)
            remoteViews.setSettingsButton(appWidgetId)
            remoteViews.setServicesList(appWidgetId)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
        }
    }

    override fun deleteWidget(appWidgetIds: IntArray) {
        appWidgetIds.forEach { deleteWidget.execute(it) }
    }

    private fun RemoteViews.setLogoButton(appWidgetId: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        setOnClickPendingIntent(R.id.widget_logo, pendingIntent)
    }

    private fun RemoteViews.setSettingsButton(appWidgetId: Int) {
        val intent = Intent(context, WidgetSettingsActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(WidgetBroadcaster.EXTRA_IS_NEW, false)
        }
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        setOnClickPendingIntent(R.id.widget_settings, pendingIntent)
    }

    private fun RemoteViews.setServicesList(appWidgetId: Int) {
        // Adapter
        setRemoteAdapter(R.id.widget_list, Intent(context, WidgetViewsService::class.java).apply {
            data = Uri.fromParts("content", appWidgetId.toString(), null)
        })

        // Empty view when adapter has no items
        setEmptyView(R.id.widget_list, R.id.widget_empty)

        // Template pending intent for handling list clicks
        val intent = Intent(context, WidgetProvider::class.java).apply {
            action = WidgetBroadcaster.ACTION_SERVICE_CLICK
            putExtra(WidgetBroadcaster.EXTRA_WIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_MUTABLE)

        setPendingIntentTemplate(R.id.widget_list, pendingIntent)
    }
}