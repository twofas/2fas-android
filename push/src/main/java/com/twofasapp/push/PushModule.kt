package com.twofasapp.push

import android.app.NotificationManager
import android.content.Context
import com.twofasapp.common.di.KoinModule
import com.twofasapp.push.data.PushLocalData
import com.twofasapp.push.data.PushLocalDataImpl
import com.twofasapp.push.domain.ObserveInAppPushesCase
import com.twofasapp.push.domain.ObserveInAppPushesCaseImpl
import com.twofasapp.push.domain.ObserveNotificationPushesCase
import com.twofasapp.push.domain.ObserveNotificationPushesCaseImpl
import com.twofasapp.push.domain.PushDispatchCase
import com.twofasapp.push.domain.PushDispatchCaseImpl
import com.twofasapp.push.domain.repository.PushLogger
import com.twofasapp.push.domain.repository.PushRepository
import com.twofasapp.push.domain.repository.PushRepositoryImpl
import com.twofasapp.push.notification.NotificationChannelProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class PushModule : KoinModule {
    override fun provide() = module {
        factory { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        singleOf(::NotificationChannelProvider)
        singleOf(::PushLogger)
        singleOf(::PushLocalDataImpl) { bind<PushLocalData>() }
        singleOf(::PushRepositoryImpl) { bind<PushRepository>() }

        singleOf(::PushDispatchCaseImpl) { bind<PushDispatchCase>() }
        singleOf(::ObserveInAppPushesCaseImpl) { bind<ObserveInAppPushesCase>() }
        singleOf(::ObserveNotificationPushesCaseImpl) { bind<ObserveNotificationPushesCase>() }
    }
}
