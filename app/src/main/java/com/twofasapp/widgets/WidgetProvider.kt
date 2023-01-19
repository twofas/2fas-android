package com.twofasapp.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.twofasapp.extensions.doNothing
import com.twofasapp.widgets.presenter.WidgetPresenter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetProvider : AppWidgetProvider(), KoinComponent {

    private val widgetPresenter: WidgetPresenter by inject()

    companion object {
        const val UPDATE_INTERVAL = 5_000L
        const val INTERACTION_TIMEOUT = 60_000L
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        widgetPresenter.deleteWidget(appWidgetIds)
        widgetPresenter.onDeleted()
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        widgetPresenter.updateWidget()
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            super.onReceive(context, intent)
            widgetPresenter.onReceive(intent)
        } catch (e: Exception) {
            doNothing()
        }
    }
}