package com.twofasapp.widgets.adapter

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetViewsAdapter(
            appWidgetId = Integer.valueOf(intent.data!!.schemeSpecificPart)
        )
    }
}