package com.twofasapp

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pluto.Pluto
import com.pluto.plugins.datastore.pref.PlutoDatastorePreferencesPlugin
import com.twofasapp.base.AuthTracker
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.logger.Flog
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.logs.LogsRepository
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.di.Modules
import com.twofasapp.logger.FlogSinkLogcat
import com.twofasapp.logger.FlogSinkPersist
import com.twofasapp.migration.MigrateDataStore
import com.twofasapp.parsers.SupportedServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.time.Instant
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration.Companion.milliseconds

class App : Application() {

    private val flogSinkLogcat: FlogSinkLogcat by inject()
    private val flogSinkPersist: FlogSinkPersist by inject()
    private val appBuild: AppBuild by inject()
    private val authTracker: AuthTracker by inject()
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val migrateDataStore: MigrateDataStore by inject()
    private val logsRepository: LogsRepository by inject()
    private val servicesRepository: ServicesRepository by inject()
    private val groupsRepository: GroupsRepository by inject()
    private val backupRepository: BackupRepository by inject()
    private val securityRepository: SecurityRepository by inject()
    private val browserExtRepository: BrowserExtRepository by inject()
    private val customizationRepository: CustomizationRepository by inject()
    private val widgetsRepository: WidgetsRepository by inject()
    private val timeProvider: TimeProvider by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initLogger()
        initUncaughtExceptionHandler()
        initSupportedServices()
        initDebugTools()

        migrationError = migrateToDataStore()
        if (migrationError != null) {
            return
        }

        initCrashlytics()
        initLifecycleObserver()
        initActivityLogger()
        initPluto()

        authTracker.onAppCreate()
        cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.AppStart)

        logLaunch()
    }

    private fun initSupportedServices() {
        try {
            SupportedServices.load(this@App)
        } catch (e: Exception) {
            e.printStackTrace()
            Flog.persist(tag = "Launch", message = "Failed to load supported services")
            Flog.persist(tag = "Launch", throwable = e)
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
                Flog.persist(tag = "Launch", message = "DataStore migration failed")
                Flog.persist(tag = "Launch", throwable = e)
                runBlocking { withTimeoutOrNull(2_000.milliseconds) { logsRepository.flush() } }
                e
            }
        }
    }

    private fun initLogger() {
        Flog.init(
            debug = appBuild.buildVariant == BuildVariant.Debug,
            sinkLogcat = flogSinkLogcat,
            sinkPersist = flogSinkPersist,
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
                Flog.persist(tag = "Lifecycle", message = "Foreground")
                authTracker.onMovingToForeground()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                Timber.d("App :: onMoveToBackground")
                Flog.persist(tag = "Lifecycle", message = "Background")
                authTracker.onMovingToBackground()
                cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.AppBackground)
            }
        })
    }

    private fun initActivityLogger() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Flog.persist(
                    tag = "Activity",
                    message = "Created ${activity.javaClass.simpleName}" + if (savedInstanceState != null) " (restored)" else "",
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                Flog.persist(tag = "Activity", message = "Destroyed ${activity.javaClass.simpleName}")
            }

            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        })
    }

    private fun initUncaughtExceptionHandler() {
        // Crashlytics installs its handler before Application.onCreate, so delegating
        // to the previous handler keeps fatal crash reporting intact.
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Flog.persist(tag = "Crash", message = "Uncaught exception on thread=${thread.name}")
                Flog.persist(tag = "Crash", throwable = throwable)
                // Timeout so a wedged flush (e.g. the crash itself is DB-related) can't
                // hang the process and prevent Crashlytics from receiving the fatal.
                runBlocking { withTimeoutOrNull(2_000.milliseconds) { logsRepository.flush() } }
            } catch (_: Throwable) {
                // Never block crash reporting
            } finally {
                previousHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun logLaunch() {
        Flog.persist(
            tag = "Launch",
            message = "model=${Build.MANUFACTURER} ${Build.MODEL}" +
                " android=${Build.VERSION.SDK_INT}" +
                " app=${appBuild.versionName}(${appBuild.versionCode})" +
                " variant=${appBuild.buildVariant}" +
                " locale=${Locale.getDefault().toLanguageTag()}" +
                " tz=${TimeZone.getDefault().id}",
        )

        GlobalScope.launch {
            try {
                val services = servicesRepository.getServices().size
                val trash = servicesRepository.observeDeletedServices().first().size
                val groups = groupsRepository.getGroups().size
                val browsers = browserExtRepository.observePairedBrowsers().first().size
                val lockMethod = securityRepository.getLockMethod()
                val cloudBackupStatus = backupRepository.observeCloudBackupStatus().first()
                val cloudSync = if (cloudBackupStatus.active) {
                    val lastSync = cloudBackupStatus.lastSyncMillis.takeIf { it > 0 }?.let { Instant.ofEpochMilli(it) } ?: "never"
                    "active encrypted=${cloudBackupStatus.encrypted} lastSync=$lastSync"
                } else {
                    "inactive"
                }

                Flog.persist(
                    tag = "Launch",
                    message = "services=$services trash=$trash groups=$groups browsers=$browsers" +
                        " widgets=${widgetsRepository.getWidgets().list.size}" +
                        " lock=$lockMethod cloudSync=$cloudSync",
                )

                val appSettings = settingsRepository.getAppSettings()
                Flog.persist(
                    tag = "Launch",
                    message = "timeDelta=${timeProvider.realCurrentTime() - timeProvider.systemCurrentTime()}ms" +
                        " autoTime=${isGlobalSettingEnabled(Settings.Global.AUTO_TIME)}" +
                        " autoTimeZone=${isGlobalSettingEnabled(Settings.Global.AUTO_TIME_ZONE)}" +
                        " notifications=${NotificationManagerCompat.from(this@App).areNotificationsEnabled()}" +
                        " hideCodes=${customizationRepository.observeHideCodes().first()}" +
                        " showNextCode=${customizationRepository.observeShowNextCode().first()}" +
                        " screenshots=${appSettings.allowScreenshots}" +
                        " crashReports=${settingsRepository.observeSendCrashLogs().first()}",
                )
            } catch (e: Exception) {
                Flog.persist(tag = "Launch", message = "Failed to collect app state")
                Flog.persist(tag = "Launch", throwable = e)
            }
        }
    }

    private fun isGlobalSettingEnabled(name: String): Boolean? {
        return runCatching { Settings.Global.getInt(contentResolver, name) == 1 }.getOrNull()
    }

    private fun initPluto() {
        Pluto.Installer(this)
            .apply {
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