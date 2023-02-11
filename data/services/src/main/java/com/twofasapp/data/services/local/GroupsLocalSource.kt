package com.twofasapp.data.services.local

import com.twofasapp.data.services.local.model.GroupEntity
import com.twofasapp.data.services.local.model.GroupsEntity
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class GroupsLocalSource(
    private val json: Json,
    private val preferences: PlainPreferences,
) {
    companion object {
        private const val KeyGroups = "groups"
    }

    fun observeGroups(): Flow<GroupsEntity> {
        return preferences.observe<String>(KeyGroups, null).map { value ->
            value?.let {
                json.decodeFromString(value)
            } ?: GroupsEntity()
        }
    }

    fun insertGroup(groupsEntity: GroupEntity) {
        val local = getGroups()

        preferences.putString(
            KeyGroups, json.encodeToString(
                local.copy(
                    list = local.list.plus(groupsEntity)
                )
            )
        )
    }

    private fun getGroups(): GroupsEntity {
        return preferences.getString(KeyGroups)?.let {
            json.decodeFromString(it)
        } ?: GroupsEntity()
    }
}