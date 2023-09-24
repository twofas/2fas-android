package com.twofasapp.di

import com.twofasapp.android.biometric.BiometricKeyProvider
import com.twofasapp.base.AuthTracker
import com.twofasapp.biometric.BiometricKeyProviderImpl
import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.environment.AppBuildImpl
import com.twofasapp.features.navigator.ActivityScopedNavigator
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.time.TimeProviderImpl
import com.twofasapp.usecases.MigrateBoxToRoomCase
import com.twofasapp.usecases.app.MigrateBoxToRoomCaseImpl
import com.twofasapp.usecases.services.PinOptionsUseCase
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import javax.inject.Provider

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
        singleOf(::AppBuildImpl) { bind<AppBuild>() }
        singleOf(::TimeProviderImpl) { bind<TimeProvider>() }
        singleOf(::BiometricKeyProviderImpl) { bind<BiometricKeyProvider>() }

        single { CheckLockStatus(get()) }
        single { AuthTracker(Provider { get() }) }
        single { PinOptionsUseCase(get()) }

        singleOf(::MigrateBoxToRoomCaseImpl) { bind<MigrateBoxToRoomCase>() }


        factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }
    }
}