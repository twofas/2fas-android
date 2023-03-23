package com.twofasapp.common.analytics

interface Analytics {
    fun captureException(exception: Throwable?)
    fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>)
}