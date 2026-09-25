package com.twofasapp.di

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pluto.plugins.datastore.pref.PlutoDatastoreWatcher
import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.storage.AppDatabase
import com.twofasapp.storage.DataStoreOwnerImpl
import com.twofasapp.storage.MIGRATION_10_11
import com.twofasapp.storage.MIGRATION_11_12
import com.twofasapp.storage.MIGRATION_1_2
import com.twofasapp.storage.MIGRATION_2_3
import com.twofasapp.storage.MIGRATION_3_4
import com.twofasapp.storage.MIGRATION_4_5
import com.twofasapp.storage.MIGRATION_5_6
import com.twofasapp.storage.MIGRATION_6_7
import com.twofasapp.storage.MIGRATION_9_10
import com.twofasapp.storage.cipher.DatabaseKeyGenerator
import com.twofasapp.storage.cipher.DatabaseKeyGeneratorRandom
import com.twofasapp.storage.cipher.GetDatabaseMasterKey
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File

class StorageModule : KoinModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "2fas-auth-datastore")

    override fun provide() = module {
        single<DataStore<Preferences>> {
            androidContext().dataStore.also {
                PlutoDatastoreWatcher.watch("2fas-auth-datastore", it)
            }
        }
        singleOf(::DataStoreOwnerImpl) { bind<DataStoreOwner>() }

        singleOf(::DatabaseKeyGeneratorRandom) { bind<DatabaseKeyGenerator>() }
        singleOf(::GetDatabaseMasterKey)

        single<AppDatabase> {
            val context = androidContext()

            try {
                System.loadLibrary("sqlcipher")
            } catch (e: UnsatisfiedLinkError) {
                reportMissingNativeLibrary(context)
                throw e
            }

            val builder = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "database-2fas",
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
            )

            val sqlCipherPassphrase = get<GetDatabaseMasterKey>().execute().toByteArray()
            val sqlCipherOpenerFactory = SupportOpenHelperFactory(sqlCipherPassphrase)
            builder.openHelperFactory(sqlCipherOpenerFactory)

            builder.build()
        }

        single { get<AppDatabase>().serviceDao() }
        single { get<AppDatabase>().pairedBrowserDao() }
        single { get<AppDatabase>().notificationDao() }
    }

    private fun reportMissingNativeLibrary(context: Context) {
        try {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
            val splits = context.applicationInfo.splitNames?.joinToString().orEmpty()
            val nativeLibs = File(context.applicationInfo.nativeLibraryDir).list()?.joinToString().orEmpty()

            with(FirebaseCrashlytics.getInstance()) {
                setCustomKey("installer", installer ?: "unknown")
                setCustomKey("splits", splits.ifEmpty { "none" })
                setCustomKey("native_libs", nativeLibs.ifEmpty { "none" })
                setCustomKey("supported_abis", Build.SUPPORTED_ABIS.joinToString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}