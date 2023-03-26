package com.twofasapp.core.analytics

interface AnalyticsService {
    fun captureException(exception: Throwable?)
    fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>)
}
