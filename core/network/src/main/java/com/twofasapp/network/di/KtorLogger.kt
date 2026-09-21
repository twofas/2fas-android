/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.network.di

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import timber.log.Timber

internal class KtorLogger {
    companion object {
        private const val Tag = "Ktor"
        private const val LineWidth = 3500
    }

    fun logRequest(
        url: String,
        method: HttpMethod,
        body: String?,
    ) {
        Timber.tag(Tag).v(
            buildString {
                append("┌─ ➡️ REQUEST: ${method.value} $url")
                body?.let { append(it.splitIntoMultiLines()) }
                append("\n└─ END")
            },
        )
    }

    fun logResponse(
        url: String,
        method: HttpMethod,
        status: HttpStatusCode,
        elapsedMs: Long,
        body: String?,
    ) {
        val icon = if (status.value in 100..299) "🟢" else "🔴"

        Timber.tag(Tag).v(
            buildString {
                append("┌─ $icon RESPONSE: ${method.value} ${status.value} $url ($elapsedMs ms)")
                body?.let { append(it.splitIntoMultiLines()) }
                append("\n└─ END")
            },
        )
    }

    private fun String.splitIntoMultiLines(): String {
        return chunked(LineWidth).joinToString { "\n│  ${it.trim()}" }
    }
}