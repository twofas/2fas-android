package com.twofasapp.feature.widget.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.ActionCallback
import com.twofasapp.feature.widget.GlanceWidget

internal class ToggleServiceAction : ActionCallback {

    companion object {
        private val paramAppWidgetId = ActionParameters.Key<Int>("appWidgetId")
        private val paramServiceId = ActionParameters.Key<Long>("serviceId")

        fun params(appWidgetId: Int, serviceId: Long): ActionParameters {
            return actionParametersOf(
                paramAppWidgetId to appWidgetId,
                paramServiceId to serviceId
            )
        }
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        with(GlanceWidget()) {
            toggleService(
                appWidgetId = parameters[paramAppWidgetId] ?: 0,
                serviceId = parameters[paramServiceId] ?: 0L,
            )

            update(context, glanceId)

            startPeriodicRefresh(context)
        }
    }
}