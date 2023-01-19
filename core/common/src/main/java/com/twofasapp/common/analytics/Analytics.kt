package com.twofasapp.common.analytics

import android.content.Context

interface Analytics {
    fun init(context: Context)
    fun captureException(exception: Throwable?)
    fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>)
}