package com.twofasapp.network.di

import com.twofasapp.common.environment.AppBuild
import com.twofasapp.di.KoinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import timber.log.Timber

class NetworkModule : KoinModule {
    override fun provide() = module {
        single {
            val isDebuggable = get<AppBuild>().isDebuggable

            HttpClient(OkHttp) {
                expectSuccess = true

                if (isDebuggable) {
                    install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                Timber.tag("Ktor").v(message)
                            }
                        }
                        level = LogLevel.ALL
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                            explicitNulls = false
                            coerceInputValues = true
                        }
                    )
                }
                install(DefaultRequest) {
                    url("https://api2.2fas.com")
                    contentType(ContentType.Application.Json)
                }
            }
        }
    }
}