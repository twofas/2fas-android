package com.twofasapp.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.twofasapp.common.analytics.Analytics
import com.twofasapp.common.analytics.AnalyticsEvent
import com.twofasapp.common.analytics.AnalyticsParam

class AnalyticsCore : Analytics {

    override fun captureException(exception: Throwable?) {
        exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
    }

    override fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>) {
    }
}