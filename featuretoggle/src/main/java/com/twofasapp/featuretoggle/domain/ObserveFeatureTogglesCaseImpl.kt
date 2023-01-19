package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveFeatureTogglesCaseImpl(
    private val featureToggleRepository: FeatureToggleRepository,
) : ObserveFeatureTogglesCase {

    override operator fun invoke(): Flow<Map<FeatureToggle, Boolean>> {
        return featureToggleRepository.observeFeatureToggles()
    }
}