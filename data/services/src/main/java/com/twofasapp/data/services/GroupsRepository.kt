package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Group
import kotlinx.coroutines.flow.Flow

interface GroupsRepository {
    fun observeGroups(): Flow<List<Group>>
    suspend fun getGroups(): List<Group>
    suspend fun addGroup(name: String)
    suspend fun addGroup(group: Group)
    suspend fun deleteGroup(id: String)
    suspend fun editGroup(id: String, name: String)
    suspend fun editGroup(group: Group)
    suspend fun moveUpGroup(id: String)
    suspend fun moveDownGroup(id: String)
    suspend fun toggleGroup(id: String?)
    suspend fun sortById(ids: List<String>)
    suspend fun markAllAsSynced()
}