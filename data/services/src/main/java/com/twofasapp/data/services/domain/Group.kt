package com.twofasapp.data.services.domain

data class Group(
    val id: String,
    val name: String,
    val isExpanded: Boolean = true,
    val updatedAt: Long = 0,
//    val backupSyncStatus: BackupSyncStatus? = BackupSyncStatus.NOT_SYNCED,
)
