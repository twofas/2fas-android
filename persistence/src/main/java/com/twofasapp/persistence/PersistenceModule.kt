package com.twofasapp.persistence

import androidx.room.Room
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.di.KoinModule
import com.twofasapp.persistence.cipher.DatabaseKeyGenerator
import com.twofasapp.persistence.cipher.DatabaseKeyGeneratorRandom
import com.twofasapp.persistence.cipher.GetDatabaseMasterKey
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class PersistenceModule : KoinModule {

    override fun provide() = module {
        singleOf(::DatabaseKeyGeneratorRandom) { bind<DatabaseKeyGenerator>() }
        singleOf(::GetDatabaseMasterKey)

        single<AppDatabase> {
            val context = androidContext()

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

            if (get<AppBuild>().debuggable.not()) {
                val factory = SupportFactory(SQLiteDatabase.getBytes(get<GetDatabaseMasterKey>().execute().toCharArray()))
                builder.openHelperFactory(factory)
            }

            builder.build()
        }

        single { get<AppDatabase>().serviceDao() }
        single { get<AppDatabase>().pairedBrowserDao() }
        single { get<AppDatabase>().notificationDao() }
    }
}