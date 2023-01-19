package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository

internal class UpdateFeatureEnabledCaseImpl(
    private val featureToggleRepository: FeatureToggleRepository,
) : UpdateFeatureEnabledCase {

    override fun execute(featureToggle: FeatureToggle, isEnabled: Boolean) {
        featureToggleRepository.updateFeatureToggle(featureToggle, isEnabled)
    }
}