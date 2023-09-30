package com.twofasapp.data.services.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackupGroup(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("isExpanded")
    val isExpanded: Boolean,
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
)