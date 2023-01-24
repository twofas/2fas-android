package com.twofasapp.data.services.di

import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.GroupsRepositoryImpl
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.ServicesRepositoryImpl
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.otp.ServiceCodeGenerator
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataServicesModule : KoinModule {
    override fun provide() = module {
        singleOf(::ServiceCodeGenerator)
        singleOf(::ServicesLocalSource)
        singleOf(::ServicesRepositoryImpl) { bind<ServicesRepository>() }

        singleOf(::GroupsRepositoryImpl) { bind<GroupsRepository>() }
    }
}