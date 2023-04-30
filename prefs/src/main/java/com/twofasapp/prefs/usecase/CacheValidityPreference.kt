package com.twofasapp.prefs.usecase

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.prefs.internals.PreferenceModel
import com.twofasapp.prefs.model.CacheEntry
import com.twofasapp.prefs.model.CacheValidity
import com.twofasapp.storage.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CacheValidityPreference(
    preferences: Preferences,
    private val timeProvider: TimeProvider,
) : PreferenceModel<CacheValidity>(preferences) {

    override val key: String = "cacheValidity"
    override val default: CacheValidity = CacheValidity(emptyMap())
    override var useCache: Boolean = false

    override val serialize: (CacheValidity) -> String = { jsonSerializer.serialize(it) }
    override val deserialize: (String) -> CacheValidity = { jsonSerializer.deserialize(it) }

    suspend fun <T> runWithCacheValidation(entry: CacheEntry, onCacheInvalid: suspend () -> T): T? {
        return withContext(Dispatchers.Default) {
            if (timeProvider.systemCurrentTime() > (get().entries[entry.key] ?: 0)) {
                try {
                    onCacheInvalid.invoke().also {
                        put { CacheValidity(it.entries.plus(entry.key to (timeProvider.systemCurrentTime() + entry.duration))) }
                    }
                } catch (e: Exception) {
                    put { CacheValidity(it.entries.minus(entry.key)) }
                    throw e
                }
            } else {
                null
            }
        }
    }

    suspend fun <T> runWithCacheValidation(entry: CacheEntry, onCacheValid: suspend () -> T, onCacheInvalid: suspend () -> T): T {
        return runWithCacheValidation(entry, onCacheInvalid) ?: onCacheValid.invoke()
    }
}