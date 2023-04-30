package com.twofasapp.time

import android.system.Os.bind
import com.twofasapp.di.KoinModule
import com.twofasapp.time.domain.RecalculateTimeDeltaCase
import com.twofasapp.time.domain.RecalculateTimeDeltaCaseImpl
import com.twofasapp.time.domain.SyncTimeCase
import com.twofasapp.time.domain.SyncTimeCaseImpl
import com.twofasapp.time.domain.formatter.DurationFormatter
import com.twofasapp.time.domain.formatter.DurationFormatterImpl
import com.twofasapp.time.domain.work.SyncTimeWorkDispatcher
import com.twofasapp.time.domain.work.SyncTimeWorkDispatcherImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class TimeModule : KoinModule {

    override fun provide() = module {
        singleOf(::SyncTimeWorkDispatcherImpl) { bind<SyncTimeWorkDispatcher>() }

        singleOf(::DurationFormatterImpl) { bind<DurationFormatter>() }

        singleOf(::SyncTimeCaseImpl) { bind<SyncTimeCase>() }
        singleOf(::RecalculateTimeDeltaCaseImpl) { bind<RecalculateTimeDeltaCase>() }
    }
}
