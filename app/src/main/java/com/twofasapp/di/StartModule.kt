package com.twofasapp.di

import com.twofasapp.android.navigation.DeeplinkHandler
import com.twofasapp.common.di.KoinModule
import com.twofasapp.migration.MigrateDataStore
import com.twofasapp.migration.MigrateUnknownServices
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcher
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcherImpl
import com.twofasapp.workmanager.SyncTimeWorkDispatcher
import com.twofasapp.workmanager.SyncTimeWorkDispatcherImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class StartModule : KoinModule {

    override fun provide() = module {
        singleOf(::DeeplinkHandler)

        singleOf(::OnAppUpdatedWorkDispatcherImpl) { bind<OnAppUpdatedWorkDispatcher>() }
        singleOf(::SyncTimeWorkDispatcherImpl) { bind<SyncTimeWorkDispatcher>() }

        singleOf(::MigrateUnknownServices)
        single { MigrateDataStore(androidContext(), get(), get()) }
    }
}