package com.twofasapp.widgets.broadcast

import android.content.Intent

interface WidgetBroadcaster {

    companion object {
        const val ACTION_AUTO_UPDATE = "ACTION_AUTO_UPDATE"
        const val ACTION_SETTINGS_CHANGED = "ACTION_SETTINGS_CHANGED"
        const val ACTION_SERVICE_CHANGED = "ACTION_SERVICE_CHANGED"
        const val ACTION_SERVICE_CLICK = "ACTION_SERVICE_CLICKED"

        const val EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_CLICK_SOURCE = "EXTRA_CLICK_SOURCE"
        const val EXTRA_CODE = "EXTRA_CODE"
        const val EXTRA_IS_NEW = "EXTRA_IS_NEW"

        const val CLICK_ITEM = 0
        const val CLICK_COPY = 1
    }

    fun sendWidgetSettingsChanged()
    fun sendServiceChanged()
    fun scheduleWidgetTimer()
    fun cancelWidgetTimer()

    fun intentAutoUpdate(): Intent
    fun intentItemClick(position: Int): Intent
    fun intentItemCopyClick(position: Int, code: String): Intent
}