package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteGroup(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("isExpanded")
    val isExpanded: Boolean,
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
) {
    fun toLocal() = com.twofasapp.prefs.model.Group(
        id = id,
        name = name,
        isExpanded = isExpanded,
        updatedAt = updatedAt,
        backupSyncStatus = BackupSyncStatus.SYNCED,
    )
}
