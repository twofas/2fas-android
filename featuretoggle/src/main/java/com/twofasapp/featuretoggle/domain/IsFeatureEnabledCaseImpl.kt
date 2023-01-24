package com.twofasapp.featuretoggle.domain

import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository

internal class IsFeatureEnabledCaseImpl(
    private val appBuild: AppBuild,
    private val featureToggleRepository: FeatureToggleRepository,
) : IsFeatureEnabledCase {

    override fun execute(featureToggle: FeatureToggle): Boolean {
        if (appBuild.buildVariant == BuildVariant.Release) {
            return featureToggle.default
        }

        return featureToggleRepository.getFeatureToggles()
            .getOrDefault(featureToggle, featureToggle.default)
    }
}