/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.about.ui.logs

import androidx.lifecycle.ViewModel
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.logs.LogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class LogsViewModel(
    private val logsRepository: LogsRepository,
    appBuild: AppBuild,
) : ViewModel() {
    val uiState = MutableStateFlow(LogsUiState(isDebuggable = appBuild.debuggable))

    init {
        loadLogs()
    }

    private fun loadLogs() {
        launchScoped {
            val logs = logsRepository.getAll()
            uiState.update { it.copy(logs = logs, isLoading = false) }
        }
    }

    fun generateShareContent(): String {
        val utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        return buildString {
            appendLine("timestamp,tag,message")
            uiState.value.logs.forEach { entry ->
                val utcTime = Instant.ofEpochMilli(entry.timestamp).atZone(ZoneOffset.UTC).format(utcFormatter)
                appendLine("$utcTime,${entry.tag},${entry.message}")
            }
        }
    }

    fun clearLogs() {
        launchScoped {
            logsRepository.deleteAll()
            uiState.update { it.copy(logs = emptyList()) }
        }
    }

    fun generateFilename(): String {
        return "2FAS_Auth_Logs_${
            Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        }.txt"
    }
}