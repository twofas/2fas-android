package com.twofasapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.twofasapp.base.AuthTracker
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.di.Modules
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.prefs.usecase.SendCrashLogsPreference
import net.sqlcipher.database.SQLiteDatabase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    private val authTracker: AuthTracker by inject()
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher by inject()
    private val sendCrashLogsPreference: SendCrashLogsPreference by inject()

    override fun onCreate() {
        super.onCreate()

        try {
            SupportedServices.load(this@App)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        startKoin {
            // androidLogger(level = Level.DEBUG)
            androidContext(this@App)
            modules(Modules.provide())
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        }

        authTracker.onAppCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                Timber.d("App :: onMoveToForeground")
                authTracker.onMovingToForeground()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                Timber.d("App :: onMoveToBackground")
                authTracker.onMovingToBackground()
                cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.AppBackground)
            }
        })

        cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.AppStart)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(sendCrashLogsPreference.get())

        try {
            SQLiteDatabase.loadLibs(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
