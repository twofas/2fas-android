package com.twofasapp.feature.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.twofasapp.feature.widget.GlanceWidget
import kotlin.random.Random

internal class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

//        updateAppWidgetState(context = context, glanceId = glanceId) { prefs ->
//            prefs.set(intPreferencesKey("uid"),Random.nextInt())
//        }

        Repo.updateWeatherInfo()
        GlanceWidget().updateAll(context)

//        GlanceWidget().update(context, glanceId)
//      updateAppWidgetState(context, glanceId){ prefs ->
//
//      }

//        dispatchWidgetsUpdate(context)
    }
}