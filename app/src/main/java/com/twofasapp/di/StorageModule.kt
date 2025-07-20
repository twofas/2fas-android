package com.twofasapp.di

import androidx.room.Room
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.twofasapp.common.di.KoinModule
import com.twofasapp.storage.AppDatabase
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

class StorageModule : KoinModule {

    override fun provide() = module {
        singleOf(::DatabaseKeyGeneratorRandom) { bind<DatabaseKeyGenerator>() }
        singleOf(::GetDatabaseMasterKey)

        single<AppDatabase> {
            val context = androidContext()

            try {
                SplitInstallHelper.loadLibrary(context, "sqlcipher")
            } catch (e: Exception) {
                System.loadLibrary("sqlcipher")
                e.printStackTrace()
            }

            val builder = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-2fas"
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
}