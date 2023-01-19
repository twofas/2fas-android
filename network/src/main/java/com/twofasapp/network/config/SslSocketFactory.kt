package com.twofasapp.network.config

import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

class SslSocketFactory {
    companion object {
        fun create(protocol: String = "SSL", trustManager: TrustManager = TrustManagerSelfSigned()): SSLSocketFactory {
            val sslContext = SSLContext.getInstance(protocol)
            sslContext.init(null, arrayOf(trustManager), SecureRandom())
            return sslContext.socketFactory
        }
    }
}