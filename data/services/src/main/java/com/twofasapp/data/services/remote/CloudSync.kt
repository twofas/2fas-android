package com.twofasapp.data.services.remote

import com.twofasapp.common.environment.AppBuild
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
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import timber.log.Timber
import java.util.Locale

class CloudSync(
    private val appBuild: AppBuild,
    private val timeProvider: TimeProvider,
    private val servicesRepository: ServicesRepository,
    private val groupsRepository: GroupsRepository,
    private val backupRepository: BackupRepository,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
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
            val error: CloudSyncError
        ) : RemoteStatus
    }

    suspend fun execute(trigger: CloudSyncTrigger, password: String?): CloudSyncResult {

        backupRepository.publishCloudSyncStatus(CloudSyncStatus.Syncing)

        Timber.d(trigger.name)

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
            CloudSyncTrigger.RemovePassword -> {
                sync(now, false, password, trigger)
            }
        }

        return when (syncBackupStatus) {
            is RemoteStatus.Success -> {
                remoteBackupStatusPreference.put {
                    it.copy(
                        lastSyncMillis = now,
                        schemaVersion = BackupContent.CurrentSchema
                    )
                }

                backupRepository.publishCloudSyncStatus(CloudSyncStatus.Synced(trigger))

                CloudSyncResult.Success
            }

            is RemoteStatus.Error -> {
                when (syncBackupStatus.error) {
                    CloudSyncError.DecryptNoPassword,
                    CloudSyncError.DecryptWrongPassword -> remoteBackupKeyPreference.delete()

                    else -> Unit
                }

                backupRepository.publishCloudSyncStatus(
                    CloudSyncStatus.Error(
                        error = syncBackupStatus.error,
                        trigger = trigger
                    )
                )

                CloudSyncResult.Failure(trigger = trigger)
            }
        }
    }

    private suspend fun sync(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: CloudSyncTrigger
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
                    return updateRemote(now, isFirstConnect, password, trigger)
                } else {
                    return remoteStatus
                }
            }
        }

        // Prepare database revisions (in our case it's timestamp)
        val backupStatus = remoteBackupStatusPreference.get()
        val localRevision = backupStatus.lastSyncMillis
        val remoteRevision = remoteStatus.lastSyncTime

        val localSchemaVersion = backupStatus.schemaVersion
        val remoteSchemaVersion = remoteStatus.schemaVersion

        val localAppVersionCode = appBuild.versionCode
        val remoteAppVersionCode = remoteStatus.appVersionCode

        // Sync matching groups
        remoteGroups.forEach { remoteGroup ->
            val matchingLocalGroup = localGroups.find { it.id == remoteGroup.id }
            compareLocalGroupsWithRemote(
                matchingLocalGroup,
                remoteGroup,
                localRevision,
                remoteRevision
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
                remoteRevision
            )
            localServices.remove(matchingLocal)
        }

        // Manage services which are present on local but missing from remote
        localServices.forEach {
            // Remove local service if service is missing from remote and was SYNCED before
            if (
                isFirstConnect.not()
                && localAppVersionCode == remoteAppVersionCode
                && localSchemaVersion == remoteSchemaVersion
                && it.backupSyncStatus == BackupSyncStatus.SYNCED
            ) {
                servicesRepository.trashService(id = it.id, triggerSync = false)
            }
        }

        // Manage groups which are present on local but missing from remote
        localGroups.forEach {
            // Remove local group if group is missing from remote and was SYNCED before
            if (
                isFirstConnect.not()
                && localAppVersionCode == remoteAppVersionCode
                && localSchemaVersion == remoteSchemaVersion
                && it.backupSyncStatus == BackupSyncStatus.SYNCED
            ) {
                it.id?.let { id -> groupsRepository.deleteGroup(id) }
            }
        }

        if (groupsRepository.getGroups().all { it.updatedAt < localRevision }) {
            groupsRepository.sortById(remoteGroups.map { it.id!! })
        }

        return updateRemote(now, isFirstConnect, password, trigger)
    }

    private suspend fun updateRemote(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: CloudSyncTrigger
    ): RemoteStatus {
        try {
            val updatedLocalServices = servicesRepository.getServices()
            val remoteKey = remoteBackupKeyPreference.get()

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
                                        updatedAt = now
                                    )
                                } else {
                                    it.copy(backupSyncStatus = BackupSyncStatus.SYNCED)
                                }
                            }
                    )

                    // Mark groups as SYNCED
                    groupsRepository.markAllAsSynced()

                    return RemoteStatus.Success(
                        services = emptyList(),
                        groups = emptyList(),
                        lastSyncTime = 0,
                        schemaVersion = BackupContent.CurrentSchema,
                        appVersionCode = 0
                    )
                }

                is CloudBackupUpdateResult.Failure -> {
                    return RemoteStatus.Error(
                        error = updateResult.error
                    )
                }
            }
        } catch (e: Exception) {
            return RemoteStatus.Error(CloudSyncError.SyncFailure)
        }
    }

    private suspend fun compareLocalServiceWithRemote(
        local: Service?,
        remote: Service,
        localRevision: Long,
        remoteRevision: Long
    ) {
        // Service exists on remote, but not on local
        if (local == null) {
            // Service was not recently deleted, add it to the list
            val recentlyDeletedServices = servicesRepository.getRecentlyDeletedServices()
            if (recentlyDeletedServices.services.firstOrNull { it.secret == remote.secret } == null) {
                servicesRepository.addService(service = remote.copy(), triggerSync = false)
                return
            }

            // Service was recently deleted
            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                // Revisions are different, for safety reasons restore the service from remote
                servicesRepository.addService(service = remote.copy(), triggerSync = false)
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
                )
            )
        }
    }

    private suspend fun compareLocalGroupsWithRemote(
        local: Group?,
        remote: Group,
        localRevision: Long,
        remoteRevision: Long
    ) {
        // Group exists on remote, but not on local
        if (local == null) {

            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                // Revisions are different - add remote group
                groupsRepository.addGroup(remote)
            }

            // Group was locally deleted or
            // revisions are the same, remove group from cloud
            return
        }

        // Groups exists on local and remote
        // Check their edit time and update if needed -> override with remote
        if (local.isContentEqualTo(remote).not() && remote.updatedAt > local.updatedAt) {
            groupsRepository.editGroup(remote)
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
                                    null
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
                                iconCollectionIdFromLegacy = iconCollectionIdFromLegacy
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
                    error = result.error
                )
            }
        }
    }
}