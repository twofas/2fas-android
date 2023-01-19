package com.twofasapp.analytics

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.twofasapp.common.analytics.Analytics
import com.twofasapp.common.analytics.AnalyticsEvent
import com.twofasapp.common.analytics.AnalyticsParam
import timber.log.Timber

class AnalyticsFirebase : Analytics {
    private val analytics: FirebaseAnalytics?
        get() {
            return try {
                Firebase.analytics
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private val crashlytics: FirebaseCrashlytics?
        get() {
            return try {
                FirebaseCrashlytics.getInstance()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override fun init(context: Context) {
        try {
            Firebase.remoteConfig
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun captureException(exception: Throwable?) {
        Timber.d("Exception :: ${exception?.message}")
    }

    override fun captureEvent(event: AnalyticsEvent, vararg params: Pair<AnalyticsParam, String?>) {
        Timber.d("Event :: ${event.name.lowercase()} [${params.joinToString { "${it.first.name.lowercase()}=${it.second}" }}]")

        analytics?.logEvent(
            event.name.lowercase(),
            bundleOf(*params.map { Pair(it.first.name.lowercase(), it.second) }
                .filter { it.second != null }.toTypedArray())
        )
    }
}