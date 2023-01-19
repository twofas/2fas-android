package com.twofasapp.network

import com.twofasapp.di.KoinModule
import com.twofasapp.environment.AppConfig
import com.twofasapp.network.api.BrowserExtensionApi
import com.twofasapp.network.api.NotificationsApi
import com.twofasapp.serialization.JsonSerializer
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import timber.log.Timber

class NetworkModule : KoinModule {

    override fun provide() = module {

//        single {
//            OkHttpClient.Builder()
//                .sslSocketFactory(SslSocketFactory.create(), TrustManagerSelfSigned())
//                .hostnameVerifier(HostVerifier())
//                .addInterceptor(get<HttpLoggingInterceptor>())
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build()
//        }

        singleOf(::BrowserExtensionApi)
        singleOf(::NotificationsApi)

        single {
            val isDebug = get<AppConfig>().isDebug

            HttpClient(OkHttp) {
                expectSuccess = true

                if (isDebug) {
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
                    json(get<JsonSerializer>().json)
                }
                install(DefaultRequest) {
                    contentType(ContentType.Application.Json)
                }
            }
        }
    }
}