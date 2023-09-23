package com.twofasapp.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.twofasapp.feature.widget.GlanceWidget
import org.koin.core.component.KoinComponent

/**
 * Never rename/move this class as it will cause widget to be removed.
 * User would have to set it up again.
 */
class WidgetProvider : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()
}