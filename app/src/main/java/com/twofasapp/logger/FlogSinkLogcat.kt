/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.logger

import android.util.Log
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.logger.FlogLevel
import com.twofasapp.common.logger.FlogSink
import timber.log.Timber

class FlogSinkLogcat(
    appBuild: AppBuild,
) : FlogSink {

    init {
        if (appBuild.buildVariant == BuildVariant.Debug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun log(level: FlogLevel, tag: String, message: String, throwable: Throwable?) {
        val priority = when (level) {
            FlogLevel.Verbose -> Log.VERBOSE
            FlogLevel.Debug -> Log.DEBUG
            FlogLevel.Info -> Log.INFO
            FlogLevel.Warn -> Log.WARN
            FlogLevel.Error -> Log.ERROR
        }

        Timber.tag(tag).log(priority, throwable, message)
    }
}