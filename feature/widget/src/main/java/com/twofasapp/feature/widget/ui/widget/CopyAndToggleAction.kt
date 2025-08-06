package com.twofasapp.feature.widget.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.ActionCallback
import com.twofasapp.designsystem.ktx.copyToClipboard

internal class CopyAndToggleAction : ActionCallback {

    companion object {
        private val paramCode = ActionParameters.Key<String>("code")
        private val paramAppWidgetId = ActionParameters.Key<Int>("appWidgetId")
        private val paramServiceId = ActionParameters.Key<Long>("serviceId")

        fun params(code: String, appWidgetId: Int, serviceId: Long): ActionParameters {
            return actionParametersOf(
                paramCode to code,
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
        // Copy to clipboard
        context.copyToClipboard(
            text = parameters[paramCode].orEmpty(),
            isSensitive = true,
            showToast = false
        )

        // Call ToggleServiceAction to avoid duplicate code
        ToggleServiceAction().onAction(context, glanceId, parameters)

    }
}
