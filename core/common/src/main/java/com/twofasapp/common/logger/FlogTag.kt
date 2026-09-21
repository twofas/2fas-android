/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.logger

class FlogTag internal constructor(
    private val tag: String,
) {
    fun v(message: String) = Flog.dispatch(FlogLevel.Verbose, tag, message)

    fun d(message: String) = Flog.dispatch(FlogLevel.Debug, tag, message)

    fun i(message: String) = Flog.dispatch(FlogLevel.Info, tag, message)

    fun w(message: String) = Flog.dispatch(FlogLevel.Warn, tag, message)

    fun e(message: String, throwable: Throwable? = null) = Flog.dispatch(FlogLevel.Error, tag, message, throwable)

    fun e(throwable: Throwable?) = Flog.dispatch(FlogLevel.Error, tag, throwable?.message ?: "", throwable)
}