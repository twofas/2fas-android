package com.twofasapp.data.services.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentlyDeleted(
    @SerialName("services")
    val services: List<RecentlyDeletedService>,
)