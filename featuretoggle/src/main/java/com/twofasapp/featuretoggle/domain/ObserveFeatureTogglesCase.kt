package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import kotlinx.coroutines.flow.Flow

interface ObserveFeatureTogglesCase {
    operator fun invoke(): Flow<Map<FeatureToggle, Boolean>>
}