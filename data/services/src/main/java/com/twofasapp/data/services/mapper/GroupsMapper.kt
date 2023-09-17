package com.twofasapp.data.services.mapper

import com.twofasapp.data.services.domain.BackupGroup
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.model.GroupEntity

internal fun GroupEntity.asDomain() = Group(
    id = id,
    name = name,
    isExpanded = isExpanded,
    updatedAt = updatedAt,
    backupSyncStatus = backupSyncStatus,
)

internal fun List<Group>.asBackup(): List<BackupGroup> {
    return mapNotNull { group ->
        BackupGroup(
            id = group.id ?: return@mapNotNull null,
            name = group.name.orEmpty(),
            isExpanded = group.isExpanded,
            updatedAt = group.updatedAt,
        )
    }
}

internal fun BackupGroup.asDomain(): Group {
    return Group(
        id = id,
        name = name,
        isExpanded = isExpanded,
        updatedAt = updatedAt,
    )
}