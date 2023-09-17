package com.twofasapp.data.services.local

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.model.GroupEntity
import com.twofasapp.data.services.local.model.GroupsEntity
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Collections
import java.util.UUID

internal class GroupsLocalSource(
    private val json: Json,
    private val preferences: PlainPreferences,
    private val timeProvider: TimeProvider,
) {
    companion object {
        private const val KeyGroups = "groups"
    }

    fun observeGroups(): Flow<GroupsEntity> {
        return preferences.observe(KeyGroups, "").map { value ->
            value?.let {
                try {
                    json.decodeFromString(value)
                } catch (e: Exception) {
                    GroupsEntity()
                }
            } ?: GroupsEntity()
        }
    }

    fun getGroups(): GroupsEntity {
        return preferences.getString(KeyGroups)?.let {
            json.decodeFromString(it)
        } ?: GroupsEntity()
    }

    fun addGroup(name: String) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = local.list.plus(
                        GroupEntity(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            isExpanded = true,
                            updatedAt = timeProvider.systemCurrentTime(),
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    ).distinctBy { it.id }
                )
            )
        )
    }

    fun addGroup(group: Group) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = local.list.plus(
                        GroupEntity(
                            id = group.id,
                            name = group.name.orEmpty(),
                            isExpanded = group.isExpanded,
                            updatedAt = timeProvider.systemCurrentTime(),
                            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                        )
                    ).distinctBy { it.id }
                )
            )
        )
    }

    fun deleteGroup(id: String) {
        val local = getGroups()
        val newList = local.list.filterNot { it.id == id }

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = newList,
                    isDefaultGroupExpanded = if (newList.isEmpty()) true else local.isDefaultGroupExpanded
                )
            )
        )
    }

    fun editGroup(id: String, name: String) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
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
                    }
                )
            )
        )
    }

    fun editGroup(newGroup: Group) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
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
                    }
                )
            )
        )
    }

    fun moveUpGroup(id: String) {
        val local = getGroups()

        val index = local.list.indexOfFirst { it.id == id }
        if (index > 0) {
            Collections.swap(local.list, index, index - 1)
        }
        preferences.putString(KeyGroups, json.encodeToString(local))
    }

    fun moveDownGroup(id: String) {
        val local = getGroups()

        val index = local.list.indexOfFirst { it.id == id }
        if (index < local.list.size - 1) {
            Collections.swap(local.list, index, index + 1)
        }
        preferences.putString(KeyGroups, json.encodeToString(local))
    }

    fun toggleGroup(id: String?) {
        val local = getGroups()

        val updated = if (id == null) {
            local.copy(isDefaultGroupExpanded = local.isDefaultGroupExpanded.not())
        } else {
            local.copy(
                list = local.list.map { group ->
                    if (group.id == id) {
                        group.copy(isExpanded = group.isExpanded.not())
                    } else {
                        group
                    }
                }.distinctBy { it.id }
            )
        }

        preferences.putString(KeyGroups, json.encodeToString(updated))
    }

    suspend fun sortById(ids: List<String>) {
        val local = getGroups()
        val sortedList = local.list.sortedBy { ids.indexOf(it.id) }

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = sortedList,
                )
            )
        )
    }

    suspend fun markAllAsSynced() {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = local.list.map { group -> group.copy(backupSyncStatus = BackupSyncStatus.SYNCED) },
                )
            )
        )
    }
}