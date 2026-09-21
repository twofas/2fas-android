package com.twofasapp.data.push.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.data.push.notification.NotificationChannelProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataPushModule : KoinModule {
    override fun provide() = module {
        singleOf(::NotificationChannelProvider)
    }
}