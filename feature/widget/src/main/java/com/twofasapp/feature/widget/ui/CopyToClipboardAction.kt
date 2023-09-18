package com.twofasapp.feature.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.ActionCallback
import com.twofasapp.designsystem.ktx.copyToClipboard

internal class CopyToClipboardAction() : ActionCallback {
    companion object {
        private val paramCode = ActionParameters.Key<String>("code")

        fun params(code: String): ActionParameters {
            return actionParametersOf(paramCode to code)
        }
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        context.copyToClipboard(
            text = parameters[paramCode].orEmpty(),
            isSensitive = true,
            showToast = false,
        )
    }
}