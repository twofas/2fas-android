package com.twofasapp.feature.widget.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.twofasapp.feature.widget.GlanceWidget

internal class HotpGenerateAction : ActionCallback {

    companion object {
        private val paramServiceId = ActionParameters.Key<Long>("serviceId")

        fun params(serviceId: Long): ActionParameters {
            return actionParametersOf(
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
            generateHotpCode(
                serviceId = parameters[paramServiceId] ?: 0L,
            )

            updateAll(context)

            startPeriodicRefresh(context)
        }
    }
}