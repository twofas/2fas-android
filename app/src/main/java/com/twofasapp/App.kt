package com.twofasapp

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.twofasapp.base.AuthTracker
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.di.Modules
import com.twofasapp.di.StartModule
import com.twofasapp.parsers.ParsersModule
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.persistence.PersistenceModule
import com.twofasapp.prefs.PreferencesEncryptedModule
import com.twofasapp.prefs.PreferencesPlainModule
import com.twofasapp.prefs.usecase.SendCrashLogsPreference
import com.twofasapp.push.PushModule
import com.twofasapp.security.SecurityModule
import com.twofasapp.services.ServicesModule
import com.twofasapp.usecases.services.PinOptionsUseCase
import net.sqlcipher.database.SQLiteDatabase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {

    private val authTracker: AuthTracker by inject()
    private val syncBackupDispatcher: CloudSyncWorkDispatcher by inject()
    private val pinOptionsUseCase: PinOptionsUseCase by inject()
    private val sendCrashLogsPreference: SendCrashLogsPreference by inject()

    override fun onCreate() {
        super.onCreate()

        try {
            SupportedServices.load(this@App)
        } catch (e: Exception) {
        }

        startKoin {
            // androidLogger(level = Level.DEBUG)
            androidContext(this@App)
            modules(
                listOf(
                    StartModule(),
                    ParsersModule(),
                    PreferencesPlainModule(),
                    PreferencesEncryptedModule(),
                    PushModule(),
                    PersistenceModule(),
                    ServicesModule(),
                    SecurityModule(),
                )
                    .map { it.provide() }
                    .plus(Modules.provide())
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        }

        authTracker.onAppCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onMoveToForeground() {
                Timber.d("App :: onMoveToForeground")
                authTracker.onMovingToForeground()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onMoveToBackground() {
                Timber.d("App :: onMoveToBackground")
                authTracker.onMovingToBackground()
                syncBackupDispatcher.tryDispatch(CloudSyncTrigger.AppBackground)
                pinOptionsUseCase.tmpDigits = null
            }
        })

        syncBackupDispatcher.tryDispatch(CloudSyncTrigger.AppStart)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(sendCrashLogsPreference.get())

        try {
            SQLiteDatabase.loadLibs(this)
        } catch (e: Exception) {
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
