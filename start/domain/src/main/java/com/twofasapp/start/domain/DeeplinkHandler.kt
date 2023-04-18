package com.twofasapp.start.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class DeeplinkHandler {
    private var deeplink: String? = null

    private val flow = MutableStateFlow<String?>(null)

    fun setQueuedDeeplink(incomingData: String?) {
        deeplink = incomingData

        deeplink?.let { flow.tryEmit(it) }
    }

    fun observeQueuedDeeplink(): Flow<String> {
        return flow.filterNotNull()
    }
}