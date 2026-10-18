/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.twofasapp.data.logs.local.model.LogEntryEntity

@Dao
interface LogsDao {
    @Insert
    suspend fun insert(logs: List<LogEntryEntity>)

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT 5000")
    suspend fun getAll(): List<LogEntryEntity>

    @Query("DELETE FROM logs")
    suspend fun deleteAll()
}