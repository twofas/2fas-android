package com.twofasapp.core.analytics

import android.content.Context

interface AnalyticsService {
    fun init(context: Context)
    fun captureException(exception: Throwable?)
    fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>)
    fun setUserCountry(countryCode: String?)
}
