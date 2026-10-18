/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.data.logs.LogsRepository
import com.twofasapp.data.logs.LogsRepositoryImpl
import com.twofasapp.data.logs.local.LogsLocalSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class DataLogsModule : KoinModule {
    override fun provide() = module {
        // Resolve the DAO lazily so logging at startup doesn't open the encrypted database
        single { LogsLocalSource(lazy { get() }) }
        singleOf(::LogsRepositoryImpl) { bind<LogsRepository>() }
    }
}