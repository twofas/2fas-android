package com.twofasapp.feature.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.twofasapp.feature.widget.sync.dispatchWidgetsUpdate

internal class ToggleServiceAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        dispatchWidgetsUpdate(context)
    }
}