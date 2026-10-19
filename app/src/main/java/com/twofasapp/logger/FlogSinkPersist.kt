/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.logger

import com.twofasapp.common.logger.FlogLevel
import com.twofasapp.common.logger.FlogSink
import com.twofasapp.data.logs.LogsRepository

class FlogSinkPersist(
    private val logsRepository: LogsRepository,
) : FlogSink {

    override fun log(level: FlogLevel, tag: String, message: String, throwable: Throwable?) {
        logsRepository.save(
            tag = tag,
            message = if (throwable != null) {
                listOfNotNull(
                    message.takeIf { it.isNotEmpty() && it != throwable.message },
                    throwable.format(),
                ).joinToString("\n")
            } else {
                message
            },
        )
    }

    private fun Throwable.format(): String {
        return buildString {
            var current: Throwable? = this@format
            var depth = 0

            while (current != null && depth < MaxCauseDepth) {
                if (depth > 0) append("\nCaused by: ")
                append("${current::class.simpleName}: ${current.message}")
                current.stackTrace.take(MaxStackFrames).forEach { append("\n  at $it") }
                current = current.cause
                depth++
            }
        }
    }

    companion object {
        private const val MaxCauseDepth = 5
        private const val MaxStackFrames = 10
    }
}