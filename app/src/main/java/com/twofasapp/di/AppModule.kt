package com.twofasapp.di

import android.app.NotificationManager
import android.content.Context
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.LockMethodProvider
import com.twofasapp.biometric.BiometricKeyProviderImpl
import com.twofasapp.common.crypto.AndroidKeyStore
import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.crypto.AndroidKeyStoreImpl
import com.twofasapp.data.push.notification.ShowBrowserExtRequestNotification
import com.twofasapp.environment.AppBuildImpl
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import com.twofasapp.locale.Strings
import com.twofasapp.logger.FlogSinkLogcat
import com.twofasapp.navigator.ActivityScopedNavigator
import com.twofasapp.navigator.LockMethodProviderImpl
import com.twofasapp.notification.ShowBrowserExtRequestNotificationImpl
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.time.TimeProviderImpl
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
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

        singleOf(::Strings)
        singleOf(::AppBuildImpl) { bind<AppBuild>() }
        singleOf(::TimeProviderImpl) { bind<TimeProvider>() }
        singleOf(::BiometricKeyProviderImpl) { bind<BiometricKeyProvider>() }
        singleOf(::AndroidKeyStoreImpl) { bind<AndroidKeyStore>() }
        singleOf(::LockMethodProviderImpl) { bind<LockMethodProvider>() }
        singleOf(::AuthTracker)
        singleOf(::FlogSinkLogcat)

        factory { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

        singleOf(::ShowBrowserExtRequestNotificationImpl) { bind<ShowBrowserExtRequestNotification>() }

        factory<ScopedNavigator> { ActivityScopedNavigator(get(), get()) }
    }
}