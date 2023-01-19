package com.twofasapp.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WidgetTimerReceiver : BroadcastReceiver(), KoinComponent {

    private val widgetBroadcaster: WidgetBroadcaster by inject()

    override fun onReceive(context: Context, intent: Intent) {
        WidgetLogger.log("Timer.OnReceive :: Schedule (${LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)})")

        // Update widget
        context.sendBroadcast(widgetBroadcaster.intentAutoUpdate())

        // Schedule next timer
        widgetBroadcaster.scheduleWidgetTimer()
    }
}