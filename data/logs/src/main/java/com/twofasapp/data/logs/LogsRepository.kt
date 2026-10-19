/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs

import com.twofasapp.data.logs.domain.LogEntry

interface LogsRepository {
    fun save(tag: String, message: String)
    suspend fun flush()
    suspend fun getAll(): List<LogEntry>
    suspend fun deleteAll()
}