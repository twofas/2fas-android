package com.twofasapp.data.services.domain

import com.twofasapp.common.domain.BackupSyncStatus

data class Group(
    val id: String?,
    val name: String?,
    val isExpanded: Boolean = true,
    val updatedAt: Long = 0,
    val backupSyncStatus: BackupSyncStatus? = BackupSyncStatus.NOT_SYNCED,
) {
    fun isContentEqualTo(other: Group): Boolean {
        return id == other.id && name == other.name && isExpanded == other.isExpanded
    }
}
