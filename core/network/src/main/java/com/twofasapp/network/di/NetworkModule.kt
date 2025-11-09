package com.twofasapp.network.di

import com.twofasapp.common.di.KoinModule
import com.twofasapp.common.environment.AppBuild
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class NetworkModule : KoinModule {
    override fun provide() = module {
        singleOf(::KtorLogger)
        singleOf(::KtorLoggingInterceptor)

        single {
            val isDebuggable = get<AppBuild>().debuggable

            HttpClient(OkHttp) {
                expectSuccess = true

                if (isDebuggable) {
                    install(get<KtorLoggingInterceptor>())
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