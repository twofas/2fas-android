package com.twofasapp.feature.widget.sync

import android.app.Application
import com.twofasapp.common.domain.WidgetCallbacks

class WidgetCallbacksImpl(
    private val context: Application
) : WidgetCallbacks {

    override fun onServiceChanged() {
        WidgetsUpdateWork.dispatch(context)
    }

    override fun onServiceDeleted(serviceId: Long) {
        WidgetsUpdateWork.dispatch(context)
    }
}