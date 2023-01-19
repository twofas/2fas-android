package com.twofasapp.featuretoggle.domain.repository

import com.twofasapp.featuretoggle.domain.model.FeatureToggle
import com.twofasapp.prefs.model.DeveloperConfigEntity
import com.twofasapp.prefs.usecase.DeveloperConfigPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FeatureToggleRepositoryImpl(
    private val developerConfigPreference: DeveloperConfigPreference
) : FeatureToggleRepository {

    private val featureToggleNames by lazy { FeatureToggle.values().map { id -> id.name } }
    private val defaultFeatureToggles: Map<FeatureToggle, Boolean> by lazy {
        FeatureToggle.values().associateWith { featureToggle -> featureToggle.default }
    }

    override fun getFeatureToggles(): Map<FeatureToggle, Boolean> {
        val savedFeatureToggles = developerConfigPreference.get().featureToggles
            .filter { featureToggleNames.contains(it.key) }
            .mapKeys { FeatureToggle.valueOf(it.key) }

        return defaultFeatureToggles.plus(savedFeatureToggles)
    }

    override fun observeFeatureToggles(): Flow<Map<FeatureToggle, Boolean>> {
        return developerConfigPreference.flow().map { entity ->
            val savedFeatureToggles = entity.featureToggles
                .filter { featureToggleNames.contains(it.key) }
                .mapKeys { FeatureToggle.valueOf(it.key) }

            defaultFeatureToggles.plus(savedFeatureToggles)
        }
    }

    override fun updateFeatureToggle(featureToggle: FeatureToggle, isEnabled: Boolean) {
        developerConfigPreference.put {
            DeveloperConfigEntity(
                featureToggles = it.featureToggles.plus(featureToggle.name to isEnabled)
            )
        }
    }
}
