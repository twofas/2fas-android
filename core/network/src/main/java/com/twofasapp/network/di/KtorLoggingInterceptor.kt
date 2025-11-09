/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.network.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.AttributeKey

internal class KtorLoggingInterceptor(
    private val logger: KtorLogger,
) : HttpClientPlugin<Unit, KtorLoggingInterceptor> {

    override val key: AttributeKey<KtorLoggingInterceptor> = AttributeKey("KtorLoggingInterceptor")

    override fun prepare(block: Unit.() -> Unit) = this

    override fun install(plugin: KtorLoggingInterceptor, scope: HttpClient) {
        scope.sendPipeline.intercept(HttpSendPipeline.Before) {
            val requestBody = when (val content = context.body) {
                is TextContent -> content.text
                is FormDataContent -> content.formData.entries().joinToString("&") { "${it.key}=${it.value.first()}" }
                else -> null
            }

            logger.logRequest(
                url = context.url.toString(),
                method = context.method,
                body = requestBody,
            )
        }

        scope.receivePipeline.intercept(HttpReceivePipeline.After) { response ->
            val elapsedMs = response.responseTime.timestamp - response.requestTime.timestamp
            val contentType = response.call.response.contentType()
            val responseBody = if (contentType?.match(ContentType.Application.Json) == true || contentType?.contentType == "text") {
                try {
                    response.call.response.bodyAsText()
                } catch (e: Exception) {
                    "<error reading body: ${e.message}>"
                }
            } else {
                null
            }

            logger.logResponse(
                url = response.request.url.toString(),
                method = response.request.method,
                status = response.call.response.status,
                elapsedMs = elapsedMs,
                body = responseBody,
            )
        }
    }
}