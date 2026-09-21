/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.logger

interface FlogSink {
    fun log(
        level: FlogLevel,
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )
}