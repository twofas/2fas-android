package com.twofasapp.di

import android.app.NotificationManager
import android.content.Context
import com.twofasapp.android.biometric.BiometricKeyProvider
import com.twofasapp.base.AuthTracker
import com.twofasapp.biometric.BiometricKeyProviderImpl
import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.push.notification.ShowBrowserExtRequestNotification
import com.twofasapp.environment.AppBuildImpl
import com.twofasapp.navigator.ActivityScopedNavigator
import com.twofasapp.notification.ShowBrowserExtRequestNotificationImpl
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.time.TimeProviderImpl
import com.twofasapp.migration.MigrateBoxToRoom
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
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

        factory { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

        single { CheckLockStatus(get()) }
        single { AuthTracker(Provider { get() }) }

        singleOf(::MigrateBoxToRoom)

        singleOf(::ShowBrowserExtRequestNotificationImpl) { bind<ShowBrowserExtRequestNotification>() }

        factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }
    }
}