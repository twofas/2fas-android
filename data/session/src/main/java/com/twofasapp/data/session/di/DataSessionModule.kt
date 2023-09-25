package com.twofasapp.data.session.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.SecurityRepositoryImpl
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SessionRepositoryImpl
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.SettingsRepositoryImpl
import com.twofasapp.data.session.local.SessionLocalSource
import com.twofasapp.data.session.local.SettingsLocalSource
import com.twofasapp.data.session.work.DisableScreenshotsWorkDispatcher
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataSessionModule : KoinModule {
    override fun provide() = module {
        singleOf(::SessionLocalSource)
        singleOf(::SessionRepositoryImpl) { bind<SessionRepository>() }

        singleOf(::SettingsLocalSource)
        singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }

        singleOf(::SecurityRepositoryImpl) { bind<SecurityRepository>() }

        singleOf(::DisableScreenshotsWorkDispatcher)
    }
}