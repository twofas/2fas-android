/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.about.ui.logs

import com.twofasapp.data.logs.domain.LogEntry

internal data class LogsUiState(
    val logs: List<LogEntry> = emptyList(),
    val isLoading: Boolean = true,
    val isDebuggable: Boolean = false,
)