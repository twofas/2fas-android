package com.twofasapp.services.analytics

import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsParam
import com.twofasapp.core.analytics.AnalyticsService

class AnalyticsServiceFirebase : AnalyticsService {

    override fun captureException(exception: Throwable?) {
    }

    override fun captureEvent(
        event: AnalyticsEvent,
        vararg params: Pair<AnalyticsParam, String?>
    ) {
    }
}
