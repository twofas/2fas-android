package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.FeatureToggle

interface UpdateFeatureEnabledCase {

    fun execute(featureToggle: FeatureToggle, isEnabled: Boolean)
}