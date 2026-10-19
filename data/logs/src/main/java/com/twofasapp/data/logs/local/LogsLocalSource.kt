/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs.local

import com.twofasapp.data.logs.local.dao.LogsDao
import com.twofasapp.data.logs.local.model.LogEntryEntity

internal class LogsLocalSource(
    dao: Lazy<LogsDao>,
) {
    private val dao by dao

    suspend fun insert(entities: List<LogEntryEntity>) = dao.insert(entities)

    suspend fun getAll(): List<LogEntryEntity> = dao.getAll()

    suspend fun deleteAll() = dao.deleteAll()
}