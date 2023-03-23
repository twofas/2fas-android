package com.twofasapp.featuretoggle

import com.twofasapp.di.KoinModule
import com.twofasapp.featuretoggle.domain.IsFeatureEnabledCase
import com.twofasapp.featuretoggle.domain.IsFeatureEnabledCaseImpl
import com.twofasapp.featuretoggle.domain.ObserveFeatureTogglesCase
import com.twofasapp.featuretoggle.domain.ObserveFeatureTogglesCaseImpl
import com.twofasapp.featuretoggle.domain.UpdateFeatureEnabledCase
import com.twofasapp.featuretoggle.domain.UpdateFeatureEnabledCaseImpl
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class FeatureToggleModule : KoinModule {
    override fun provide() = module {
        singleOf(::FeatureToggleRepositoryImpl) { bind<FeatureToggleRepository>() }

        singleOf(::ObserveFeatureTogglesCaseImpl) { bind<ObserveFeatureTogglesCase>() }
        singleOf(::IsFeatureEnabledCaseImpl) { bind<IsFeatureEnabledCase>() }
        singleOf(::UpdateFeatureEnabledCaseImpl) { bind<UpdateFeatureEnabledCase>() }
    }
}
