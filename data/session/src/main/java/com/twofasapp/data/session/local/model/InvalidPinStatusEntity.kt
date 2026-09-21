package com.twofasapp.data.session.local.model

import kotlinx.serialization.Serializable

@Serializable
internal data class InvalidPinStatusEntity(
    val attempts: Int = 0,
    val lastAttemptSinceBootMs: Long = 0,
)