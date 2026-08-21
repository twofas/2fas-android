/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.feature.developer.ui.DeveloperViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class DeveloperModule : KoinModule {
    override fun provide() = module {
        viewModelOf(::DeveloperViewModel)
    }
}