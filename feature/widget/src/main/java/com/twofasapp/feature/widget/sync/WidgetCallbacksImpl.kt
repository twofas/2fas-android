package com.twofasapp.feature.widget.sync

import android.app.Application
import com.twofasapp.common.domain.WidgetCallbacks
import com.twofasapp.data.services.WidgetsRepository
import javax.inject.Provider

class WidgetCallbacksImpl(
    private val context: Application,
    private val widgetsRepository: Provider<WidgetsRepository>,
) : WidgetCallbacks {

    override suspend fun onServiceChanged() {
        if (widgetsRepository.get().getWidgets().list.isNotEmpty()) {
            WidgetsUpdateWork.dispatch(context)
        }
    }

    override suspend fun onServiceDeleted(serviceId: Long) {
        if (widgetsRepository.get().getWidgets().list.isNotEmpty()) {
            WidgetsUpdateWork.dispatch(context)
        }
    }
}