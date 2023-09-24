package com.twofasapp.time

import com.twofasapp.common.di.KoinModule
import com.twofasapp.time.domain.RecalculateTimeDeltaCase
import com.twofasapp.time.domain.RecalculateTimeDeltaCaseImpl
import com.twofasapp.time.domain.SyncTimeCase
import com.twofasapp.time.domain.SyncTimeCaseImpl
import com.twofasapp.time.domain.work.SyncTimeWorkDispatcher
import com.twofasapp.time.domain.work.SyncTimeWorkDispatcherImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class TimeModule : KoinModule {

    override fun provide() = module {
        singleOf(::SyncTimeWorkDispatcherImpl) { bind<SyncTimeWorkDispatcher>() }


        singleOf(::SyncTimeCaseImpl) { bind<SyncTimeCase>() }
        singleOf(::RecalculateTimeDeltaCaseImpl) { bind<RecalculateTimeDeltaCase>() }
    }
}
