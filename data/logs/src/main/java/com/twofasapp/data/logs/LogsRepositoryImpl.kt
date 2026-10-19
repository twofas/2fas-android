/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.data.logs

import com.twofasapp.data.logs.domain.LogEntry
import com.twofasapp.data.logs.local.LogsLocalSource
import com.twofasapp.data.logs.local.model.LogEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LogsRepositoryImpl(
    private val localSource: LogsLocalSource,
) : LogsRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val buffer = MutableStateFlow(emptyList<LogEntryEntity>())

    init {
        scope.launch {
            buffer
                .filter { it.isNotEmpty() }
                .sample(FlushInterval)
                .collect { flushPending() }
        }
    }

    override fun save(tag: String, message: String) {
        buffer.update {
            it + LogEntryEntity(
                tag = tag,
                timestamp = System.currentTimeMillis(),
                message = message,
            )
        }
    }

    override suspend fun flush() {
        flushPending()
    }

    override suspend fun getAll(): List<LogEntry> {
        flushPending()

        return localSource.getAll().map { entity ->
            LogEntry(
                id = entity.id,
                tag = entity.tag,
                timestamp = entity.timestamp,
                message = entity.message,
            )
        }
    }

    override suspend fun deleteAll() {
        buffer.update { emptyList() }
        localSource.deleteAll()
    }

    private suspend fun flushPending() {
        withContext(Dispatchers.IO) {
            val pending = buffer.getAndUpdate { emptyList() }
            if (pending.isNotEmpty()) localSource.insert(pending)
        }
    }

    companion object {
        private const val FlushInterval = 5_000L
    }
}