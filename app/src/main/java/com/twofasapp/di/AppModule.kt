package com.twofasapp.di

import com.twofasapp.analytics.AnalyticsFirebase
import com.twofasapp.common.analytics.Analytics
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.environment.AppBuildImpl
import com.twofasapp.time.TimeProviderImpl
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class AppModule : KoinModule {
    override fun provide() = module {
        single {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
                coerceInputValues = true
            }
        }
        singleOf(::AnalyticsFirebase) { bind<Analytics>() }
        singleOf(::AppBuildImpl) { bind<AppBuild>() }
        singleOf(::TimeProviderImpl) { bind<TimeProvider>() }
    }
}