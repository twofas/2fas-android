package com.twofasapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pluto.Pluto
import com.pluto.plugins.datastore.pref.PlutoDatastorePreferencesPlugin
import com.pluto.plugins.rooms.db.PlutoRoomsDatabasePlugin
import com.twofasapp.base.AuthTracker
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.logger.Flog
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.di.Modules
import com.twofasapp.logger.FlogSinkLogcat
import com.twofasapp.migration.MigrateDataStore
import com.twofasapp.parsers.SupportedServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    private val flogSinkLogcat: FlogSinkLogcat by inject()
    private val appBuild: AppBuild by inject()
    private val authTracker: AuthTracker by inject()
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val migrateDataStore: MigrateDataStore by inject()

    override fun onCreate() {
        super.onCreate()

        initSupportedServices()
        initKoin()
        initDebugTools()

        migrationError = migrateToDataStore()
        if (migrationError != null) {
            return
        }

        initLogger()
        initCrashlytics()
        initLifecycleObserver()
        initPluto()

        authTracker.onAppCreate()
        cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.AppStart)
    }

    private fun initSupportedServices() {
        try {
            SupportedServices.load(this@App)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(Modules.provide())
        }
    }

    private fun initDebugTools() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            System.setProperty("kotlinx.coroutines.debug", "on")
        }
    }

    private fun migrateToDataStore(): Throwable? {
        return runBlocking(Dispatchers.IO) {
            try {
                migrateDataStore.invoke()
                null
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e
            }
        }
    }

    private fun initLogger() {
        Flog.init(
            debug = appBuild.buildVariant == BuildVariant.Debug,
            sinkLogcat = flogSinkLogcat,
        )
    }

    private fun initCrashlytics() {
        runBlocking {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = settingsRepository.observeSendCrashLogs().first()
        }
    }

    private fun initLifecycleObserver() {
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
    }

    private fun initPluto() {
        Pluto.Installer(this)
            .apply {
                addPlugin(PlutoRoomsDatabasePlugin())
                addPlugin(PlutoDatastorePreferencesPlugin())
            }
            .install()
    }

    companion object {
        @Volatile
        var migrationError: Throwable? = null
            private set
    }
}