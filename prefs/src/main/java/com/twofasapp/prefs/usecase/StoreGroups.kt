package com.twofasapp.prefs.usecase

import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.di.BackupSyncStatus.NOT_SYNCED
import com.twofasapp.time.domain.TimeProvider
import java.util.*

class StoreGroups(
    private val groupsPreference: GroupsPreference,
    private val timeProvider: TimeProvider,
) {

    fun all(): com.twofasapp.prefs.model.Groups {
        return groupsPreference.get()
    }

    fun append(newGroups: List<com.twofasapp.prefs.model.Group>) {
        val groups = all()
        groups.list.map { group -> group.id }

        newGroups.forEach { remoteGroup ->
            if (groups.list.find { it.id == remoteGroup.id } == null) {
                groups.list.add(
                    com.twofasapp.prefs.model.Group(
                        id = remoteGroup.id,
                        name = remoteGroup.name,
                        isExpanded = remoteGroup.isExpanded,
                        updatedAt = remoteGroup.updatedAt,
                        backupSyncStatus = NOT_SYNCED
                    )
                )
            }
        }

        groupsPreference.put(groups)
    }

    fun updateOrder(groups: List<com.twofasapp.prefs.model.Group>) {
        groupsPreference.put(all().copy(list = groups.map { it.copy(updatedAt = timeProvider.systemCurrentTime(), backupSyncStatus = NOT_SYNCED) }
            .toMutableList()))
    }

    fun sortById(ids: List<String>) {
        val groups = all()
        groups.list.sortBy { ids.indexOf(it.id) }
        groupsPreference.put(groups)
    }

    fun add(group: com.twofasapp.prefs.model.Group) {
        val groups = all()
        groups.list.add(group.copy(updatedAt = timeProvider.systemCurrentTime(), backupSyncStatus = NOT_SYNCED))

        groupsPreference.put(groups)
    }

    fun edit(group: com.twofasapp.prefs.model.Group) {
        val groups = all()
        groups.list.set(
            groups.list.map { it.id }.indexOf(group.id),
            group.copy(updatedAt = timeProvider.systemCurrentTime(), backupSyncStatus = NOT_SYNCED)
        )

        groupsPreference.put(groups)
    }

    fun delete(group: com.twofasapp.prefs.model.Group) {
        val groups = all()
        groups.list.remove(group)

        groupsPreference.put(groups)
    }

    fun moveUp(group: com.twofasapp.prefs.model.Group) {
        val groups = all()
        val index = groups.list.map { it.id }.indexOf(group.id)
        Collections.swap(groups.list, index, index - 1)

        groupsPreference.put(groups)
    }

    fun moveDown(group: com.twofasapp.prefs.model.Group) {
        val groups = all()
        val index = groups.list.map { it.id }.indexOf(group.id)
        Collections.swap(groups.list, index, index + 1)

        groupsPreference.put(groups)
    }

    fun toggleDefaultGroupExpanded() {
        val groups = all()
        groupsPreference.put(groups.copy(isDefaultGroupExpanded = groups.isDefaultGroupExpanded.not()))
    }

    fun markAllSynced() {
        val groups = all()
        groupsPreference.put(groups.copy(list = groups.list.map { it.copy(backupSyncStatus = BackupSyncStatus.SYNCED) }.toMutableList()))
    }
}
