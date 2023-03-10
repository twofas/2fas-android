package com.twofasapp.data.services.mapper

import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.model.GroupEntity

internal fun GroupEntity.asDomain() = Group(
    id = id,
    name = name,
    isExpanded = isExpanded,
    updatedAt = updatedAt,
//    backupSyncStatus = backupSyncStatus,
)