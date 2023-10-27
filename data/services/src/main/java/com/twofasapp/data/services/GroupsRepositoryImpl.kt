package com.twofasapp.data.services

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.GroupsLocalSource
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class GroupsRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val local: GroupsLocalSource,
    private val localServices: ServicesLocalSource,
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) : GroupsRepository {

    override fun observeGroups(): Flow<List<Group>> {
        return local.observeGroups()
            .map { groups ->
                listOf(
                    // Default group
                    Group(
                        id = null,
                        name = null,
                        isExpanded = groups.isDefaultGroupExpanded
                    )
                )
                    .plus(groups.list.map { it.asDomain() })
                    .distinctBy { it.id }
            }
    }

    override suspend fun getGroups(): List<Group> {
        return observeGroups().first()
    }

    override suspend fun addGroup(name: String) {
        withContext(dispatchers.io) {
            local.addGroup(name)
            localServices.cleanUpGroups(local.getGroups().ids)
        }

        if (remoteBackupStatusPreference.get().state == RemoteBackupStatusEntity.State.ACTIVE) {
            cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.GroupsChanged)
        }
    }

    override suspend fun addGroup(group: Group) {
        withContext(dispatchers.io) {
            local.addGroup(group)
            localServices.cleanUpGroups(local.getGroups().ids)
        }
    }

    override suspend fun deleteGroup(id: String) {
        withContext(dispatchers.io) {
            local.deleteGroup(id)
            localServices.cleanUpGroups(local.getGroups().ids)
        }
    }

    override suspend fun editGroup(id: String, name: String) {
        withContext(dispatchers.io) {
            local.editGroup(id, name)
        }
    }

    override suspend fun editGroup(group: Group) {
        withContext(dispatchers.io) {
            local.editGroup(group)
        }
    }

    override suspend fun moveUpGroup(id: String) {
        withContext(dispatchers.io) {
            local.moveUpGroup(id)
        }
    }

    override suspend fun toggleGroup(id: String?) {
        withContext(dispatchers.io) {
            local.toggleGroup(id)
        }
    }

    override suspend fun moveDownGroup(id: String) {
        withContext(dispatchers.io) {
            local.moveDownGroup(id)
        }
    }

    override suspend fun sortById(ids: List<String>) {
        withContext(dispatchers.io) {
            local.sortById(ids)
        }
    }

    override suspend fun markAllAsSynced() {
        withContext(dispatchers.io) {
            local.markAllAsSynced()
        }
    }
}