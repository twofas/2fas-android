/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs.domain

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class LogEntry(
    val id: Long,
    val tag: String,
    val timestamp: Long,
    val message: String,
) {
    val formattedTime: String
        get() = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
}