package com.twofasapp.data.services.local.model

import com.twofasapp.common.domain.BackupSyncStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class GroupEntity(
    val id: String?,
    val name: String,
    val isExpanded: Boolean = true,
    val updatedAt: Long = 0,
    val backupSyncStatus: BackupSyncStatus,
) {

//    companion object {
//        fun generateNew(name: String) = GroupEntity(
//            id = UUID.randomUUID().toString(),
//            name = name,
//        )
//    }
//
//    fun isContentEqualTo(group: GroupEntity) =
//        name == group.name &&
//                id == group.id &&
//                isExpanded == group.isExpanded
//
//    fun toRemote() = RemoteGroup(
//        id = id!!,
//        name = name,
//        isExpanded = isExpanded,
//        updatedAt = updatedAt
//    )
}