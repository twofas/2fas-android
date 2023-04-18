package com.twofasapp.data.notifications.di

import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.notifications.NotificationsRepositoryImpl
import com.twofasapp.data.notifications.local.NotificationsLocalSource
import com.twofasapp.data.notifications.remote.NotificationsRemoteSource
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataNotificationsModule : KoinModule {
    override fun provide() = module {
        singleOf(::NotificationsLocalSource)
        singleOf(::NotificationsRemoteSource)
        singleOf(::NotificationsRepositoryImpl) { bind<NotificationsRepository>() }
    }
}