package com.twofasapp.widgets.presenter

import android.appwidget.AppWidgetManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.extensions.doNothing
import com.twofasapp.usecases.widgets.DeactivateAllWidgets
import com.twofasapp.usecases.widgets.GetWidgetSettings
import com.twofasapp.usecases.widgets.UpdateWidget
import com.twofasapp.widgets.R
import com.twofasapp.widgets.WidgetLogger
import com.twofasapp.widgets.WidgetProvider
import com.twofasapp.widgets.broadcast.WidgetBroadcaster

class WidgetReceiverPresenterDelegateImpl(
    private val context: Context,
    private val getWidgetSettings: GetWidgetSettings,
    private val updateWidget: UpdateWidget,
    private val timeProvider: TimeProvider,
    private val widgetBroadcaster: WidgetBroadcaster,
    private val deactivateAllWidgets: DeactivateAllWidgets,
) : WidgetReceiverPresenterDelegate {

    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(context)

    private val appWidgetIds: IntArray
        get() = appWidgetManager.getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))

    override fun onReceive(intent: Intent) {
        val appWidgetId = intent.getIntExtra(WidgetBroadcaster.EXTRA_WIDGET_ID, -1)
        WidgetLogger.log(
            "Presenter.OnReceive :: ${intent.action}${
                if (intent.action == WidgetBroadcaster.ACTION_SERVICE_CLICK) " ${
                    intent.getIntExtra(
                        WidgetBroadcaster.EXTRA_CLICK_SOURCE,
                        -1
                    )
                }" else ""
            } ${if (appWidgetId != -1) "(${appWidgetId})" else ""}"
        )

        when (intent.action!!) {
            WidgetBroadcaster.ACTION_AUTO_UPDATE,
            WidgetBroadcaster.ACTION_SETTINGS_CHANGED,
            WidgetBroadcaster.ACTION_SERVICE_CHANGED -> {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
            }
            WidgetBroadcaster.ACTION_SERVICE_CLICK -> {
                val position = intent.getIntExtra(WidgetBroadcaster.EXTRA_POSITION, -1)
                val widget = getWidgetSettings.execute().getWidgetForId(appWidgetId)!!
                val service = widget.services[position]

                when (intent.getIntExtra(WidgetBroadcaster.EXTRA_CLICK_SOURCE, -1)) {
                    WidgetBroadcaster.CLICK_ITEM -> {
                        widget.services[position] = service.copy(isActive = service.isActive.not())
                        updateWidget.execute(widget.copy(lastInteractionTimestamp = timeProvider.systemCurrentTime()))
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
                    }
                    WidgetBroadcaster.CLICK_COPY -> {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Service Code", intent.getStringExtra(WidgetBroadcaster.EXTRA_CODE)))
                    }
                }
            }
            else -> doNothing()
        }
    }

    override fun onDeleted() {
        widgetBroadcaster.cancelWidgetTimer()
        deactivateAllWidgets.execute()
    }

    override fun onDataSetChanged() {
        if (hasAnyActiveServices()) {

            if (isLastInteractionTimeoutElapsed()) {
                widgetBroadcaster.cancelWidgetTimer()
                deactivateAllWidgets.execute()
            } else {
                widgetBroadcaster.scheduleWidgetTimer()
            }

        } else {
            widgetBroadcaster.cancelWidgetTimer()
        }
    }

    private fun hasAnyActiveServices() =
        getWidgetSettings.execute().widgets.flatMap { it.services }.any { it.isActive }

    private fun isLastInteractionTimeoutElapsed() =
        timeProvider.systemCurrentTime() - getWidgetSettings.execute().widgets.maxOf { it.lastInteractionTimestamp } > WidgetProvider.INTERACTION_TIMEOUT
}