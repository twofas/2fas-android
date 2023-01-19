package com.twofasapp.start.ui.onboarding

import com.twofasapp.base.BaseViewModel
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.start.domain.EditShowOnboardingCase

internal class OnboardingViewModel(
    private val editShowOnboardingCase: EditShowOnboardingCase,
    private val analyticsService: AnalyticsService,
) : BaseViewModel() {

    fun onSkipClicked() {
        analyticsService.captureEvent(AnalyticsEvent.ONBOARDING_SKIP_CLICK)
        markOnboardingAsShown()
    }

    fun onStartUsingClicked() {
        analyticsService.captureEvent(AnalyticsEvent.ONBOARDING_FINISH_CLICK)
        markOnboardingAsShown()
    }

    fun onTermsClicked() {
        analyticsService.captureEvent(AnalyticsEvent.ONBOARDING_TERMS_CLICK)
    }

    fun onNextClicked(position: Int) {
        if (position == 0) {
            analyticsService.captureEvent(AnalyticsEvent.ONBOARDING_INIT_CLICK)
        }
        markOnboardingAsShown()
    }

    private fun markOnboardingAsShown() {
        editShowOnboardingCase(shouldShowOnboarding = false)
    }
}