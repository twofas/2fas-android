package com.twofasapp.data.services.local

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.local.model.GroupEntity
import com.twofasapp.data.services.local.model.GroupsEntity
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
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

    private fun getGroups(): GroupsEntity {
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
                            backupSyncStatus = "" // TODO
                        )
                    )
                )
            )
        )
    }

    fun deleteGroup(id: String) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(list = local.list.filterNot { it.id == id })
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
                            group.copy(name = name)
                        } else {
                            group
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
                }
            )
        }

        preferences.putString(KeyGroups, json.encodeToString(updated))
    }
}