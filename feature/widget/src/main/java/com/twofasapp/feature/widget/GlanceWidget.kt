package com.twofasapp.feature.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.feature.widget.sync.WidgetsDeleteWork
import com.twofasapp.feature.widget.sync.WidgetsPeriodicRefreshWork
import com.twofasapp.feature.widget.sync.WidgetsUpdateWork
import com.twofasapp.feature.widget.ui.widget.WidgetContent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GlanceWidget : GlanceAppWidget(), KoinComponent {

    private val widgetsRepository: WidgetsRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    appWidgetId = appWidgetId,
                    widgetsRepository = widgetsRepository,
                )
            }
        }
    }

    internal suspend fun toggleService(appWidgetId: Int, serviceId: Long) {
        widgetsRepository.toggleService(appWidgetId, serviceId)
    }

    internal suspend fun generateHotpCode(serviceId: Long) {
        widgetsRepository.generateHotpCode(serviceId)
    }

    internal suspend fun startPeriodicRefresh(context: Context) {
        widgetsRepository.incrementLastInteraction()
        WidgetsPeriodicRefreshWork.dispatch(context)
    }

    fun runUpdateWork(context: Context) {
        WidgetsUpdateWork.dispatch(context)
    }

    fun deleteWidget(context: Context, appWidgetIds: List<Int>) {
        WidgetsDeleteWork.dispatch(context, appWidgetIds)
    }
}