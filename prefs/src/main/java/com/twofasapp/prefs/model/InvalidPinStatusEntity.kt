package com.twofasapp.prefs.model

import kotlinx.serialization.Serializable

@Serializable
data class InvalidPinStatusEntity(
    val attempts: Int = 0,
    val lastAttemptSinceBootMs: Long = 0
)