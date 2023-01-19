package com.twofasapp.usecases.services

class StoreRecentlyDeleted(
    private val recentlyDeletedPreference: com.twofasapp.prefs.usecase.RecentlyDeletedPreference,
) {

    fun add(recentlyDeletedService: com.twofasapp.prefs.model.RecentlyDeletedService) {
        getRecentlyDeleted().let {
            recentlyDeletedPreference.put(it.copy(services = it.services.plus(recentlyDeletedService)))
        }
    }

    fun remove(secret: String) {
        getRecentlyDeleted().let {
            val index = it.services.indexOfFirst { service -> service.secret == secret }

            if (index > -1) {
                recentlyDeletedPreference.put(it.copy(services = it.services.filter { service -> service.secret != secret }))
            }
        }
    }

    fun get(secret: String): com.twofasapp.prefs.model.RecentlyDeletedService? {
        return getRecentlyDeleted().services.firstOrNull { it.secret == secret }
    }

    private fun getRecentlyDeleted(): com.twofasapp.prefs.model.RecentlyDeleted {
        return recentlyDeletedPreference.get()
    }
}