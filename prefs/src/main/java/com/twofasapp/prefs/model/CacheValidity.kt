package com.twofasapp.prefs.model

import kotlinx.serialization.Serializable

@Serializable
data class CacheValidity(
    internal val entries: Map<String, Long> = emptyMap()
)