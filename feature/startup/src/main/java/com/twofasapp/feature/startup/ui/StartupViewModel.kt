package com.twofasapp.feature.startup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.analytics.Analytics
import com.twofasapp.common.analytics.AnalyticsEvent
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.launch

class StartupViewModel(
    private val dispatchers: Dispatchers,
    private val analytics: Analytics,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    fun onSkipClicked() {
        analytics.captureEvent(AnalyticsEvent.ONBOARDING_SKIP_CLICK)
        markOnboardingAsShown()
    }

    fun onStartUsingClicked() {
        analytics.captureEvent(AnalyticsEvent.ONBOARDING_FINISH_CLICK)
        markOnboardingAsShown()
    }

    fun onTermsClicked() {
        analytics.captureEvent(AnalyticsEvent.ONBOARDING_TERMS_CLICK)
    }

    fun onNextClicked(position: Int) {
        if (position == 0) {
            analytics.captureEvent(AnalyticsEvent.ONBOARDING_INIT_CLICK)
        }
        markOnboardingAsShown()
    }

    private fun markOnboardingAsShown() {
        viewModelScope.launch(dispatchers.io) {
            sessionRepository.setOnboardingDisplayed(true)
        }
    }
}