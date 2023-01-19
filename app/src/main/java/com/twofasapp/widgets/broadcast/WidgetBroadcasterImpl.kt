package com.twofasapp.widgets.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.twofasapp.widgets.WidgetProvider
import com.twofasapp.widgets.WidgetTimerReceiver
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.ACTION_SERVICE_CHANGED
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.ACTION_SETTINGS_CHANGED
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.CLICK_COPY
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.CLICK_ITEM
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.EXTRA_CLICK_SOURCE
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.EXTRA_CODE
import com.twofasapp.widgets.broadcast.WidgetBroadcaster.Companion.EXTRA_POSITION

class WidgetBroadcasterImpl(
    private val context: Context,
) : WidgetBroadcaster {

    private val alarmManager: AlarmManager
        get() = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

    override fun sendWidgetSettingsChanged() = send(ACTION_SETTINGS_CHANGED)

    override fun sendServiceChanged() = send(ACTION_SERVICE_CHANGED)

    override fun scheduleWidgetTimer() {
        if (canScheduleAlarm()) {
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + WidgetProvider.UPDATE_INTERVAL,
                getTimerIntent()
            )
        }
    }

    override fun cancelWidgetTimer() {
        alarmManager.cancel(getTimerIntent())
    }

    override fun intentItemClick(position: Int) =
        Intent().apply {
            putExtra(EXTRA_CLICK_SOURCE, CLICK_ITEM)
            putExtra(EXTRA_POSITION, position)
        }

    override fun intentAutoUpdate() =
        Intent(context, WidgetProvider::class.java).apply {
            action = WidgetBroadcaster.ACTION_AUTO_UPDATE
        }

    override fun intentItemCopyClick(position: Int, code: String) =
        Intent().apply {
            putExtra(EXTRA_CLICK_SOURCE, CLICK_COPY)
            putExtra(EXTRA_POSITION, position)
            putExtra(EXTRA_CODE, code)
        }

    private fun send(intentAction: String, classType: Class<*> = WidgetProvider::class.java) =
        context.sendBroadcast(Intent(context, classType).apply { action = intentAction })

    private fun getTimerIntent() =
        PendingIntent.getBroadcast(
            context,
            98564,
            Intent(context, WidgetTimerReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun canScheduleAlarm() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
}