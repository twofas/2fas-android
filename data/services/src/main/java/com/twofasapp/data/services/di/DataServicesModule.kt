package com.twofasapp.data.services.di

import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.BackupRepositoryImpl
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.GroupsRepositoryImpl
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.ServicesRepositoryImpl
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.data.services.WidgetsRepositoryImpl
import com.twofasapp.data.services.local.GroupsLocalSource
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.otp.ServiceCodeGenerator
import com.twofasapp.data.services.remote.CloudSync
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.data.services.remote.WipeGoogleDriveWorkDispatcher
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataServicesModule : KoinModule {
    override fun provide() = module {
        singleOf(::ServiceCodeGenerator)
        singleOf(::ServicesLocalSource)
        singleOf(::ServicesRepositoryImpl) { bind<ServicesRepository>() }

        singleOf(::GroupsLocalSource)
        singleOf(::GroupsRepositoryImpl) { bind<GroupsRepository>() }

        singleOf(::BackupRepositoryImpl) { bind<BackupRepository>() }

        singleOf(::WidgetsRepositoryImpl) { bind<WidgetsRepository>() }

        singleOf(::CloudSync)
        singleOf(::WipeGoogleDriveWorkDispatcher)
        singleOf(::CloudSyncWorkDispatcher)
    }
}