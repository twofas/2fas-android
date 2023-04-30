package com.twofasapp.services.domain

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.prefs.model.ServiceDto

class StoreHotpServices(
    private val timeProvider: TimeProvider,
) {
    companion object {
        private const val BLOCK_TIME = 5000
    }

    private val hotpServices: MutableMap<String, Long> = mutableMapOf()

    fun onRefreshCounter(serviceDto: ServiceDto) {
        hotpServices.put(serviceDto.secret, timeProvider.systemCurrentTime())
    }

    fun onServiceAdded(serviceDto: ServiceDto) {
        hotpServices.put(serviceDto.secret, 0)
    }

    fun onServiceAdded(secret: String) {
        hotpServices.put(secret, 0)
    }

    fun shouldEnableRefresh(serviceDto: ServiceDto): Boolean {
        return hotpServices.contains(serviceDto.secret).not() || timeProvider.systemCurrentTime() > hotpServices[serviceDto.secret]!! + BLOCK_TIME
    }

    fun shouldShowCode(serviceDto: ServiceDto): Boolean {
        return hotpServices.contains(serviceDto.secret)
    }

    fun clear() {
        hotpServices.clear()
    }
}