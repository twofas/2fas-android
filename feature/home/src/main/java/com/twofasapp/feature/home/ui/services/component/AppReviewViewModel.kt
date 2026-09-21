package com.twofasapp.feature.home.ui.services.component

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

internal class AppReviewViewModel(
    context: Context,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val reviewManager = ReviewManagerFactory.create(context)

    fun rate(activity: Activity) {
        launchScoped {
            try {
                val reviewInfo = reviewManager.requestReview()
                reviewManager.launchReview(activity, reviewInfo)
            } catch (e: Exception) {
                Timber.tag("AppReview").e(e, "Failed to launch in-app review flow")
            } finally {
                delay(500.milliseconds)
                sessionRepository.markAppReviewPrompted()
            }
        }
    }

    fun dismiss() {
        launchScoped {
            sessionRepository.markAppReviewPrompted()
        }
    }
}