package com.twofasapp.analytics

import com.twofasapp.common.analytics.Analytics
import com.twofasapp.common.analytics.AnalyticsEvent
import com.twofasapp.common.analytics.AnalyticsParam

class AnalyticsCore : Analytics {

    override fun captureException(exception: Throwable?) {
    }

    override fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>) {
    }
}