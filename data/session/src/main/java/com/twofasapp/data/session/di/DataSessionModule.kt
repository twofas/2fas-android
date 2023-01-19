package com.twofasapp.data.session.di

import com.twofasapp.data.session.SessionRepository
import com.twofasapp.data.session.SessionRepositoryImpl
import com.twofasapp.data.session.local.SessionLocalSource
import com.twofasapp.data.session.local.SessionLocalSourceImpl
import com.twofasapp.di.KoinModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataSessionModule : KoinModule {
    override fun provide() = module {
        singleOf(::SessionLocalSourceImpl) { bind<SessionLocalSource>() }
        singleOf(::SessionRepositoryImpl) { bind<SessionRepository>() }
    }
}