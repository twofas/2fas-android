package com.twofasapp.feature.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.feature.widget.sync.dispatchWidgetsUpdate
import com.twofasapp.feature.widget.ui.WidgetContent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GlanceWidget : GlanceAppWidget(), KoinComponent {

    private val widgetsRepository: WidgetsRepository by inject()

    val tmpState = MutableStateFlow(0)

//    override val stateDefinition: GlanceStateDefinition<*>
//        get() = PreferencesGlanceStateDefinition
//    override val stateDefinition: GlanceStateDefinition<Widgets>
//        get() = object : GlanceStateDefinition<Widgets> {
//            override suspend fun getDataStore(context: Context, fileKey: String): DataStore<Widgets> {
//
//                return WidgetsStore(widgetsRepository)
//            }
//
//            override fun getLocation(context: Context, fileKey: String): File {
//                return File("") // Do nothing
//            }
//        }
//
//
//    private class WidgetsStore(private val widgetsRepository: WidgetsRepository) : DataStore<Widgets> {
//        override val data: Flow<Widgets>
//            get() = flow { emit(widgetsRepository.getWidgets()) }
//
//        override suspend fun updateData(transform: suspend (t: Widgets) -> Widgets): Widgets {
//            return transform(data.first()) // Do nothing
//        }
//    }


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        provideContent {
            GlanceTheme {

                WidgetContent(
                    onToggleService = {
                        GlobalScope.launch { widgetsRepository.toggleService(it) }
                        dispatchWidgetsUpdate(context)
                    }
                )
            }
        }
    }
}