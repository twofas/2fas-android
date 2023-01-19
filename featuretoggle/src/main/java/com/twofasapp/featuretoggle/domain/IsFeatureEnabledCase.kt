package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.FeatureToggle

interface IsFeatureEnabledCase {

    fun execute(featureToggle: FeatureToggle): Boolean
}