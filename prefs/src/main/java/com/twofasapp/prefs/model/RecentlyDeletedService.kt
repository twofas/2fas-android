package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentlyDeletedService(
    @SerialName("secret")
    val secret: String,
    @SerialName("deletedAt")
    val deletedAt: Long,
)