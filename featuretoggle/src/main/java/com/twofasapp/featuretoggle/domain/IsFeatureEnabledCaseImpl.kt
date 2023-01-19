package com.twofasapp.featuretoggle.domain

import com.twofasapp.environment.AppConfig
import com.twofasapp.environment.BuildVariant
import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository

internal class IsFeatureEnabledCaseImpl(
    private val appConfig: AppConfig,
    private val featureToggleRepository: FeatureToggleRepository,
) : IsFeatureEnabledCase {

    override fun execute(featureToggle: FeatureToggle): Boolean {
        if (appConfig.buildVariant == BuildVariant.Release) {
            return featureToggle.default
        }

        return featureToggleRepository.getFeatureToggles()
            .getOrDefault(featureToggle, featureToggle.default)
    }
}