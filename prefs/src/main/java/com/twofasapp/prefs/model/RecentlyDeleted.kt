package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentlyDeleted(
    @SerialName("services")
    val services: List<RecentlyDeletedService>
)
