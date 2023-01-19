package com.twofasapp.prefs.model

import java.time.Duration

sealed class CacheEntry(
    internal val key: String,
    internal val duration: Long,
) {
    object FetchNotifications : CacheEntry("fetchNotifications", Duration.ofSeconds(1).toMillis())
}