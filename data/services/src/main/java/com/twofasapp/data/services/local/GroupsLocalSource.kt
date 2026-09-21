package com.twofasapp.data.services.local

import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.serializedPref
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.model.GroupEntity
import com.twofasapp.data.services.local.model.GroupsEntity
import kotlinx.coroutines.flow.Flow
import java.util.Collections
import java.util.UUID

internal class GroupsLocalSource(
    dataStoreOwner: DataStoreOwner,
    private val timeProvider: TimeProvider,
) : DataStoreOwner by dataStoreOwner {

    private val groups by serializedPref(
        name = "groups",
        default = GroupsEntity(),
        serializer = GroupsEntity.serializer(),
    )

    fun observeGroups(): Flow<GroupsEntity> {
        return groups.asFlow()
    }

    suspend fun getGroups(): GroupsEntity {
        return groups.get()
    }

    suspend fun addGroup(name: String) {
        updateGroups { local ->
            local.copy(
                list = local.list.plus(
                    GroupEntity(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        isExpanded = true,
                        updatedAt = timeProvider.systemCurrentTime(),
                        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    ),
                ).distinctBy { it.id },
            )
        }
    }

    suspend fun addGroup(group: Group) {
        updateGroups { local ->
            local.copy(
                list = local.list.plus(
                    GroupEntity(
                        id = group.id,
                        name = group.name.orEmpty(),
                        isExpanded = group.isExpanded,
                        updatedAt = timeProvider.systemCurrentTime(),
                        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    ),
                ).distinctBy { it.id },
            )
        }
    }

    suspend fun deleteGroup(id: String) {
        updateGroups { local ->
            val newList = local.list.filterNot { it.id == id }

            local.copy(
                list = newList,
                isDefaultGroupExpanded = if (newList.isEmpty()) true else local.isDefaultGroupExpanded,
            )
        }
    }

    suspend fun editGroup(id: String, name: String) {
        updateGroups { local ->
            local.copy(
                list = local.list.map { group ->
                    if (group.id == id) {
                        group.copy(
                            name = name,
                            updatedAt = timeProvider.systemCurrentTime(),
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    } else {
                        group
                    }
                },
            )
        }
    }

    suspend fun editGroup(newGroup: Group) {
        updateGroups { local ->
            local.copy(
                list = local.list.map { groupEntity ->
                    if (groupEntity.id == newGroup.id) {
                        GroupEntity(
                            id = newGroup.id,
                            name = newGroup.name.orEmpty(),
                            isExpanded = newGroup.isExpanded,
                            updatedAt = newGroup.updatedAt,
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    } else {
                        groupEntity
                    }
                },
            )
        }
    }

    suspend fun moveUpGroup(id: String) {
        updateGroups { local ->
            val newList = local.list.toMutableList()
            val index = newList.indexOfFirst { it.id == id }
            if (index > 0) {
                Collections.swap(newList, index, index - 1)
            }

            local.copy(
                list = newList.map { groupEntity ->
                    if (groupEntity.id == id) {
                        groupEntity.copy(
                            updatedAt = timeProvider.systemCurrentTime(),
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    } else {
                        groupEntity
                    }
                },
            )
        }
    }

    suspend fun moveDownGroup(id: String) {
        updateGroups { local ->
            val newList = local.list.toMutableList()
            val index = newList.indexOfFirst { it.id == id }
            if (index < newList.size - 1) {
                Collections.swap(newList, index, index + 1)
            }

            local.copy(
                list = newList.map { groupEntity ->
                    if (groupEntity.id == id) {
                        groupEntity.copy(
                            updatedAt = timeProvider.systemCurrentTime(),
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    } else {
                        groupEntity
                    }
                },
            )
        }
    }

    suspend fun toggleGroup(id: String?) {
        updateGroups { local ->
            if (id == null) {
                local.copy(isDefaultGroupExpanded = local.isDefaultGroupExpanded.not())
            } else {
                local.copy(
                    list = local.list.map { group ->
                        if (group.id == id) {
                            group.copy(isExpanded = group.isExpanded.not())
                        } else {
                            group
                        }
                    }.distinctBy { it.id },
                )
            }
        }
    }

    suspend fun sortById(ids: List<String>) {
        updateGroups { local ->
            local.copy(
                list = local.list.sortedBy { ids.indexOf(it.id) },
            )
        }
    }

    suspend fun markAllAsSynced() {
        updateGroups { local ->
            local.copy(
                list = local.list.map { group -> group.copy(backupSyncStatus = BackupSyncStatus.SYNCED) },
            )
        }
    }

    private suspend fun updateGroups(action: (GroupsEntity) -> GroupsEntity) {
        groups.set(action(groups.get()))
    }
}