package com.twofasapp.data.services.remote

import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.common.domain.Service
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.logger.Flog
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.BackupContent
import com.twofasapp.data.services.domain.CloudBackupGetResult
import com.twofasapp.data.services.domain.CloudBackupUpdateResult
import com.twofasapp.data.services.domain.CloudSyncError
import com.twofasapp.data.services.domain.CloudSyncStatus
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.local.BackupLocalSource
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import timber.log.Timber
import java.util.Locale

class CloudSync(
    private val appBuild: AppBuild,
    private val timeProvider: TimeProvider,
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
    private val backupRepository: BackupRepository,
    private val backupLocalSource: BackupLocalSource,
) {
    private sealed interface RemoteStatus {
        data class Success(
            val services: List<Service>,
            val groups: List<Group>,
            val lastSyncTime: Long,
            val schemaVersion: Int,
            val appVersionCode: Int,
        ) : RemoteStatus

        data class Error(
            val error: CloudSyncError,
        ) : RemoteStatus
    }

    suspend fun execute(trigger: CloudSyncTrigger, password: String?): CloudSyncResult {
        backupRepository.publishCloudSyncStatus(CloudSyncStatus.Syncing)

        Timber.d(trigger.name)
        Flog.persist(tag = "CloudSync", message = "Started: trigger=$trigger")

        val now = timeProvider.systemCurrentTime()
        val syncBackupStatus = when (trigger) {
            CloudSyncTrigger.FirstConnect -> {
                sync(now, true, password, trigger)
            }

            CloudSyncTrigger.EnterPassword -> {
                sync(now, true, password, trigger)
            }

            CloudSyncTrigger.ServicesChanged,
            CloudSyncTrigger.GroupsChanged,
            CloudSyncTrigger.AppBackground,
            CloudSyncTrigger.AppStart,
            CloudSyncTrigger.SetPassword,
            CloudSyncTrigger.RemovePassword,
            -> {
                sync(now, false, password, trigger)
            }
        }

        return when (syncBackupStatus) {
            is RemoteStatus.Success -> {
                backupLocalSource.updateRemoteBackupStatus {
                    it.copy(
                        lastSyncMillis = syncBackupStatus.lastSyncTime,
                        schemaVersion = BackupContent.CurrentSchema,
                    )
                }

                backupRepository.publishCloudSyncStatus(CloudSyncStatus.Synced(trigger))
                Flog.persist(tag = "CloudSync", message = "Synced: trigger=$trigger")

                CloudSyncResult.Success
            }

            is RemoteStatus.Error -> {
                Flog.persist(tag = "CloudSync", message = "Failed: trigger=$trigger error=${syncBackupStatus.error}")

                when (syncBackupStatus.error) {
                    CloudSyncError.DecryptNoPassword,
                    CloudSyncError.DecryptWrongPassword,
                    -> backupRepository.deleteRemoteBackupKey()

                    else -> Unit
                }

                backupRepository.publishCloudSyncStatus(
                    CloudSyncStatus.Error(
                        error = syncBackupStatus.error,
                        trigger = trigger,
                    ),
                )

                CloudSyncResult.Failure(trigger = trigger)
            }
        }
    }

    private suspend fun sync(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: CloudSyncTrigger,
    ): RemoteStatus {
        // Prepare lists of services and groups
        val localServices = servicesRepository.getServices().toMutableList()
        val localGroups = groupsRepository.getGroups().toMutableList()
        val remoteServices = mutableListOf<Service>()
        val remoteGroups = mutableListOf<Group>()

        // Check remote status
        val remoteStatus = getRemoteBackup(password)

        when (remoteStatus) {
            is RemoteStatus.Success -> {
                remoteServices.addAll(remoteStatus.services)
                remoteGroups.addAll(remoteStatus.groups)
            }

            is RemoteStatus.Error -> {
                if (remoteStatus.error == CloudSyncError.FileNotFound) {
                    // Backup does not exists - just push all from local
                    Flog.persist(tag = "CloudSync", message = "Remote backup not found, uploading local")
                    return updateRemote(now, isFirstConnect, password, trigger)
                } else {
                    return remoteStatus
                }
            }
        }

        // Prepare database revisions (in our case it's timestamp)
        val backupStatus = backupLocalSource.getRemoteBackupStatus()

        val localRevision = backupStatus.lastSyncMillis
        val remoteRevision = remoteStatus.lastSyncTime

        val localSchemaVersion = backupStatus.schemaVersion
        val remoteSchemaVersion = remoteStatus.schemaVersion

        val localAppVersionCode = appBuild.versionCode
        val remoteAppVersionCode = remoteStatus.appVersionCode

        Flog.persist(
            tag = "CloudSync",
            message = "Remote: services=${remoteServices.size} groups=${remoteGroups.size}" +
                " revision=$remoteRevision schema=$remoteSchemaVersion app=$remoteAppVersionCode" +
                " | Local: services=${localServices.size} groups=${localGroups.size}" +
                " revision=$localRevision schema=$localSchemaVersion app=$localAppVersionCode",
        )

        val mergeSummary = MergeSummary()

        // Sync matching groups
        remoteGroups.forEach { remoteGroup ->
            val matchingLocalGroup = localGroups.find { it.id == remoteGroup.id }
            compareLocalGroupsWithRemote(
                matchingLocalGroup,
                remoteGroup,
                localRevision,
                remoteRevision,
                mergeSummary,
            )
            localGroups.remove(matchingLocalGroup)
        }

        // Sync matching services
        remoteServices.forEach { remote ->
            val matchingLocal = localServices.find {
                it.secret.lowercase(Locale.US) == remote.secret.lowercase(Locale.US)
            }
            compareLocalServiceWithRemote(
                matchingLocal,
                remote,
                localRevision,
                remoteRevision,
                mergeSummary,
            )
            localServices.remove(matchingLocal)
        }

        // Manage services which are present on local but missing from remote
        localServices.forEach {
            // Remove local service if service is missing from remote and was SYNCED before
            if (
                isFirstConnect.not() &&
                localAppVersionCode == remoteAppVersionCode &&
                localSchemaVersion == remoteSchemaVersion &&
                it.backupSyncStatus == BackupSyncStatus.SYNCED
            ) {
                servicesRepository.trashService(id = it.id, triggerSync = false)
                mergeSummary.servicesTrashed.add(it.id)
            }
        }

        // Manage groups which are present on local but missing from remote
        localGroups.forEach {
            // Remove local group if group is missing from remote and was SYNCED before
            if (
                isFirstConnect.not() &&
                localAppVersionCode == remoteAppVersionCode &&
                localSchemaVersion == remoteSchemaVersion &&
                it.backupSyncStatus == BackupSyncStatus.SYNCED
            ) {
                it.id?.let { id ->
                    groupsRepository.deleteGroup(id)
                    mergeSummary.groupsDeleted.add(id)
                }
            }
        }

        if (groupsRepository.getGroups().all { it.updatedAt < localRevision }) {
            groupsRepository.sortById(remoteGroups.map { it.id!! })
        }

        Flog.persist(tag = "CloudSync", message = mergeSummary.format())

        return updateRemote(now, isFirstConnect, password, trigger)
    }

    private suspend fun updateRemote(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: CloudSyncTrigger,
    ): RemoteStatus {
        try {
            val updatedLocalServices = servicesRepository.getServices()
            val remoteKey = backupRepository.getRemoteBackupKey()

            val updateResult = backupRepository.updateCloudBackup(
                firstConnect = isFirstConnect,
                updatedAt = now,
                password = if (trigger == CloudSyncTrigger.RemovePassword) null else password,
                keyEncoded = if (trigger == CloudSyncTrigger.RemovePassword) null else remoteKey.keyEncoded.ifEmpty { null },
                saltEncoded = if (trigger == CloudSyncTrigger.RemovePassword) null else remoteKey.saltEncoded.ifEmpty { null },
            )

            when (updateResult) {
                is CloudBackupUpdateResult.Success -> {
                    // Doesn't work as expected - commented out for now
                    // Update order on first connect
                    // if (removeIfNotPresentOnRemote.not()) {
                    //    storeServicesOrder.saveOrder(
                    //        ServicesOrder(
                    //            updatedLocalServices.sortedBy { localService ->
                    //                (remoteStatus as RemoteStatus.Success).services.map { it.secret }.indexOf(localService.secret)
                    //           }.map { it.id }
                    //        )
                    //    )
                    // }

                    // Mark services as SYNCED
                    servicesRepository.updateServicesFromCloud(
                        updatedLocalServices
                            .filter {
                                if (isFirstConnect) {
                                    true
                                } else {
                                    it.backupSyncStatus == BackupSyncStatus.NOT_SYNCED
                                }
                            }
                            .map {
                                if (isFirstConnect) {
                                    it.copy(
                                        backupSyncStatus = BackupSyncStatus.SYNCED,
                                        updatedAt = now,
                                    )
                                } else {
                                    it.copy(backupSyncStatus = BackupSyncStatus.SYNCED)
                                }
                            },
                    )

                    // Mark groups as SYNCED
                    groupsRepository.markAllAsSynced()

                    return RemoteStatus.Success(
                        services = emptyList(),
                        groups = emptyList(),
                        lastSyncTime = now,
                        schemaVersion = BackupContent.CurrentSchema,
                        appVersionCode = 0,
                    )
                }

                is CloudBackupUpdateResult.Failure -> {
                    return RemoteStatus.Error(
                        error = updateResult.error,
                    )
                }
            }
        } catch (e: Exception) {
            Flog.persist(tag = "CloudSync", message = "Update remote failed")
            Flog.persist(tag = "CloudSync", throwable = e)
            return RemoteStatus.Error(CloudSyncError.SyncFailure)
        }
    }

    private suspend fun compareLocalServiceWithRemote(
        local: Service?,
        remote: Service,
        localRevision: Long,
        remoteRevision: Long,
        mergeSummary: MergeSummary,
    ) {
        // Service exists on remote, but not on local
        if (local == null) {
            // Service was not recently deleted, add it to the list
            val recentlyDeletedServices = servicesRepository.getRecentlyDeletedServices()
            if (recentlyDeletedServices.services.firstOrNull { it.secret == remote.secret } == null) {
                mergeSummary.servicesAdded.add(servicesRepository.addService(service = remote.copy(), triggerSync = false))
                return
            }

            // Service was recently deleted
            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                // Revisions are different, for safety reasons restore the service from remote
                mergeSummary.servicesRestored.add(servicesRepository.addService(service = remote.copy(), triggerSync = false))
                servicesRepository.removeRecentlyDeleted(remote.secret)
                return
            }

            // Service was recently deleted
            // Revisions are the same, remove service from cloud
            servicesRepository.removeRecentlyDeleted(remote.secret)
            return
        }

        // Services exists on local and remote
        // Check their edit time and update if needed -> override with remote
        if (remote.updatedAt > local.updatedAt) {
            servicesRepository.updateService(
                remote.copy(
                    id = local.id,
                    assignedDomains = local.assignedDomains,
                ),
            )
            mergeSummary.servicesUpdated.add(local.id)
        }
    }

    private suspend fun compareLocalGroupsWithRemote(
        local: Group?,
        remote: Group,
        localRevision: Long,
        remoteRevision: Long,
        mergeSummary: MergeSummary,
    ) {
        // Group exists on remote, but not on local
        if (local == null) {
            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                // Revisions are different - add remote group
                groupsRepository.addGroup(remote)
                mergeSummary.groupsAdded++
            }

            // Group was locally deleted or
            // revisions are the same, remove group from cloud
            return
        }

        // Groups exists on local and remote
        // Check their edit time and update if needed -> override with remote
        if (local.isContentEqualTo(remote).not() && remote.updatedAt > local.updatedAt) {
            groupsRepository.editGroup(remote)
            mergeSummary.groupsUpdated++
        }
    }

    private suspend fun getRemoteBackup(password: String?): RemoteStatus {
        return when (val result = backupRepository.getCloudBackup(password)) {
            is CloudBackupGetResult.Success -> {
                RemoteStatus.Success(
                    services = result.backupContent.services
                        .sortedBy { it.order.position }
                        .map { backupService ->
                            val serviceTypeIdFromLegacy = backupService.type?.name?.let { type ->
                                LegacyTypeToId.serviceIds.getOrDefault(
                                    type,
                                    null,
                                )
                            }
                            var iconCollectionIdFromLegacy =
                                ServiceIcons.getIconCollection(serviceTypeIdFromLegacy.orEmpty())
                            val brandId = backupService.icon?.brand?.id?.name
                            if (brandId.isNullOrBlank().not()) {
                                val serviceTypeIdForBrand =
                                    LegacyTypeToId.serviceIds.getOrDefault(brandId!!, null)
                                if (serviceTypeIdForBrand.isNullOrBlank().not()) {
                                    iconCollectionIdFromLegacy =
                                        ServiceIcons.getIconCollection(serviceTypeIdForBrand!!)
                                }
                            }
                            if (iconCollectionIdFromLegacy.isBlank() || iconCollectionIdFromLegacy == ServiceIcons.defaultCollectionId) {
                                val serviceTypeIdForManualName =
                                    LegacyTypeToId.manualNames.getOrDefault(backupService.name.lowercase(), null)
                                if (serviceTypeIdForManualName.isNullOrBlank().not()) {
                                    iconCollectionIdFromLegacy =
                                        ServiceIcons.getIconCollection(serviceTypeIdForManualName!!)
                                }
                            }

                            backupService.asDomain(
                                serviceTypeIdFromLegacy = serviceTypeIdFromLegacy,
                                iconCollectionIdFromLegacy = iconCollectionIdFromLegacy,
                            )
                        },
                    groups = result.backupContent.groups.map { it.asDomain() },
                    lastSyncTime = result.backupContent.updatedAt,
                    schemaVersion = result.backupContent.schemaVersion,
                    appVersionCode = result.backupContent.appVersionCode,
                )
            }

            is CloudBackupGetResult.Failure -> {
                RemoteStatus.Error(
                    error = result.error,
                )
            }
        }
    }
}

/**
 * Collects local changes made while merging the remote backup, so they are persisted as a single log entry.
 */
private class MergeSummary {
    val servicesAdded = mutableListOf<Long>()
    val servicesRestored = mutableListOf<Long>()
    val servicesUpdated = mutableListOf<Long>()
    val servicesTrashed = mutableListOf<Long>()
    val groupsDeleted = mutableListOf<String>()
    var groupsAdded = 0
    var groupsUpdated = 0

    fun format(): String {
        val parts = listOfNotNull(
            servicesAdded.formatIds("added"),
            servicesRestored.formatIds("restored"),
            servicesUpdated.formatIds("updated"),
            servicesTrashed.formatIds("trashed"),
            "groupsAdded=$groupsAdded".takeIf { groupsAdded > 0 },
            "groupsUpdated=$groupsUpdated".takeIf { groupsUpdated > 0 },
            "groupsDeleted=${groupsDeleted.size}".takeIf { groupsDeleted.isNotEmpty() },
        )

        return if (parts.isEmpty()) "Merged: no local changes" else "Merged: ${parts.joinToString(" ")}"
    }

    private fun List<Long>.formatIds(label: String): String? {
        if (isEmpty()) return null

        val ids = take(MaxIds).joinToString(",") + if (size > MaxIds) ",+${size - MaxIds} more" else ""
        return "$label=$size[$ids]"
    }

    private companion object {
        const val MaxIds = 50
    }
}