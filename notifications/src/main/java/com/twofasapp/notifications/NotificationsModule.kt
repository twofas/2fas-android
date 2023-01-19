package com.twofasapp.notifications

import com.twofasapp.di.KoinModule
import com.twofasapp.notifications.data.NotificationsLocalData
import com.twofasapp.notifications.data.NotificationsLocalDataImpl
import com.twofasapp.notifications.data.NotificationsRemoteData
import com.twofasapp.notifications.data.NotificationsRemoteDataImpl
import com.twofasapp.notifications.domain.*
import com.twofasapp.notifications.domain.repository.NotificationsRepository
import com.twofasapp.notifications.domain.repository.NotificationsRepositoryImpl
import com.twofasapp.notifications.ui.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class NotificationsModule : KoinModule {

    override fun provide() = module {
        singleOf(::NotificationsLocalDataImpl) { bind<NotificationsLocalData>() }
        singleOf(::NotificationsRemoteDataImpl) { bind<NotificationsRemoteData>() }
        singleOf(::NotificationsRepositoryImpl) { bind<NotificationsRepository>() }

        singleOf(::FetchNotificationsCaseImpl) { bind<FetchNotificationsCase>() }
        singleOf(::ObserveNotificationsCase)
        singleOf(::GetNotificationsCase)
        singleOf(::ReadAllNotificationsCase)
        singleOf(::HasUnreadNotificationsCaseImpl) { bind<HasUnreadNotificationsCase>() }

        viewModelOf(::NotificationsViewModel)
    }
}