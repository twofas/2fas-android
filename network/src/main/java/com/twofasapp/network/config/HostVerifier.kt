package com.twofasapp.network.config

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class HostVerifier(private val host: String? = null, private val trustByDefault: Boolean = true) : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean {
        if (host == null) {
            return trustByDefault
        } else {
            return host.lowercase().trim().contains(hostname.lowercase().trim())
        }
    }
}