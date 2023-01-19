package com.twofasapp.featuretoggle.domain.repository

import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import kotlinx.coroutines.flow.Flow

internal interface FeatureToggleRepository {
    fun getFeatureToggles(): Map<FeatureToggle, Boolean>
    fun observeFeatureToggles(): Flow<Map<FeatureToggle, Boolean>>
    fun updateFeatureToggle(featureToggle: FeatureToggle, isEnabled: Boolean)
}