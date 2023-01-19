package com.twofasapp.prefs.model

import kotlinx.serialization.Serializable

@Serializable
data class DeveloperConfigEntity(
    val featureToggles: Map<String, Boolean> = emptyMap()
)
