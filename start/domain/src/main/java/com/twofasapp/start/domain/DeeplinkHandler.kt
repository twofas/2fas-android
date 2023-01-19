package com.twofasapp.start.domain

class DeeplinkHandler {
    private var deeplink: String? = null

    fun setQueuedDeeplink(incomingData: String?) {
        deeplink = incomingData
    }

    fun getQueuedDeeplink(): String? {
        val tmp = deeplink
        deeplink = null
        return tmp
    }
}