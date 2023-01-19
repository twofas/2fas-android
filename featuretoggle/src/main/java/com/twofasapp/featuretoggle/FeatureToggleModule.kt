package com.twofasapp.featuretoggle

import com.twofasapp.di.KoinModule
import com.twofasapp.featuretoggle.domain.*
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepository
import com.twofasapp.featuretoggle.domain.repository.FeatureToggleRepositoryImpl
import com.twofasapp.featuretoggle.domain.repository.RemoteConfigRepository
import com.twofasapp.featuretoggle.domain.repository.RemoteConfigRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class FeatureToggleModule : KoinModule {
    override fun provide() = module {
        singleOf(::FeatureToggleRepositoryImpl) { bind<FeatureToggleRepository>() }
        singleOf(::RemoteConfigRepositoryImpl) { bind<RemoteConfigRepository>() }

        singleOf(::ObserveFeatureTogglesCaseImpl) { bind<ObserveFeatureTogglesCase>() }
        singleOf(::IsFeatureEnabledCaseImpl) { bind<IsFeatureEnabledCase>() }
        singleOf(::UpdateFeatureEnabledCaseImpl) { bind<UpdateFeatureEnabledCase>() }
        singleOf(::FetchRemoteConfigCaseImpl) { bind<FetchRemoteConfigCase>() }
        singleOf(::ObserveRemoteConfigCaseImpl) { bind<ObserveRemoteConfigCase>() }
    }
}
