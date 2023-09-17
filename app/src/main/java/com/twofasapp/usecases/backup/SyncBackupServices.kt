package com.twofasapp.usecases.backup

import android.annotation.SuppressLint
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.entity.SyncBackupResult
import com.twofasapp.extensions.doNothing
import com.twofasapp.parsers.LegacyTypeToId
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.Group
import com.twofasapp.prefs.model.Groups
import com.twofasapp.prefs.model.RemoteBackup
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.backup.models.GetRemoteBackupResult
import com.twofasapp.services.backup.models.RemoteBackupErrorType
import com.twofasapp.services.backup.models.UpdateRemoteBackupResult
import com.twofasapp.services.backup.usecases.GetRemoteBackup
import com.twofasapp.services.backup.usecases.UpdateRemoteBackup
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.EditServiceUseCase
import com.twofasapp.usecases.backup.model.SyncStatus
import com.twofasapp.usecases.services.AddService
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.usecases.services.StoreRecentlyDeleted
import com.twofasapp.usecases.services.TrashService
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import java.util.Locale

@SuppressLint("CheckResult")
class SyncBackupServices(
    private val getRemoteBackup: GetRemoteBackup,
    private val updateRemoteBackup: UpdateRemoteBackup,
    private val servicesRepository: ServicesRepository,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
    private val addService: AddService,
    private val editService: EditServiceUseCase,
    private val trashService: TrashService,
    private val getServices: GetServices,
    private val storeGroups: StoreGroups,
    private val timeProvider: TimeProvider,
    private val storeRecentlyDeleted: StoreRecentlyDeleted,
    private val observeSyncStatus: ObserveSyncStatus,
    private val appBuild: AppBuild,
) : UseCaseParameterized<SyncBackupServices.Params, Single<SyncBackupResult>> {

    data class Params(
        val syncBackupTrigger: SyncBackupTrigger,
        val password: String?
    )

    sealed class RemoteStatus {
        data class Success(
            val services: List<ServiceDto>,
            val groups: Groups?,
            val lastSyncTime: Long,
            val schemaVersion: Int,
            val appVersionCode: Int,
        ) : RemoteStatus()

        data class Error(
            val operation: Operation,
            val type: RemoteBackupErrorType,
            val throwable: Throwable?
        ) : RemoteStatus()
    }

    enum class Operation { GET, UPDATE, SYNC }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun execute(
        params: Params,
        subscribeScheduler: Scheduler,
        observeScheduler: Scheduler
    ): Single<SyncBackupResult> =
        Single.create<SyncBackupResult> { emitter ->

            observeSyncStatus.publish(SyncStatus.Syncing)

            Timber.d(params.syncBackupTrigger.name)

            val now = timeProvider.systemCurrentTime()
            val syncBackupStatus = when (params.syncBackupTrigger) {
                SyncBackupTrigger.FirstConnect -> {
                    sync(now, true, params.password, params.syncBackupTrigger)
                }

                SyncBackupTrigger.EnterPassword -> {
                    sync(now, true, params.password, params.syncBackupTrigger)
                }

                SyncBackupTrigger.ServicesChanged,
                SyncBackupTrigger.GroupsChanged,
                SyncBackupTrigger.AppBackground,
                SyncBackupTrigger.AppStart,
                SyncBackupTrigger.SetPassword,
                SyncBackupTrigger.RemovePassword -> {
                    sync(now, false, params.password, params.syncBackupTrigger)
                }
            }

            when (syncBackupStatus) {
                is RemoteStatus.Success -> {
                    remoteBackupStatusPreference.put {
                        it.copy(
                            lastSyncMillis = now,
                            schemaVersion = RemoteBackup.CURRENT_SCHEMA
                        )
                    }

                    observeSyncStatus.publish(SyncStatus.Synced(trigger = params.syncBackupTrigger))

                    emitter.onSuccess(SyncBackupResult.Success())
                }

                is RemoteStatus.Error -> {
                    when (syncBackupStatus.type) {
                        RemoteBackupErrorType.DECRYPT_NO_PASSWORD,
                        RemoteBackupErrorType.DECRYPT_WRONG_PASSWORD -> remoteBackupKeyPreference.delete()

                        else -> doNothing()
                    }

                    observeSyncStatus.publish(
                        SyncStatus.Error(
                            type = syncBackupStatus.type,
                            trigger = params.syncBackupTrigger
                        )
                    )
                    emitter.onSuccess(SyncBackupResult.Failure(params.syncBackupTrigger))
                }
            }
        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    private fun sync(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: SyncBackupTrigger
    ): SyncBackupServices.RemoteStatus {
        // Prepare lists of services and groups
        val localServices = getLocalServices().toMutableList()
        val localGroups = storeGroups.all().let { it.copy(list = it.list.toMutableList()) }
        val remoteServices = mutableListOf<ServiceDto>()
        val remoteGroups = mutableListOf<Group>()

        // Check remote status
        val remoteStatus = getRemoteBackup(password)
        when (remoteStatus) {
            is RemoteStatus.Success -> {
                remoteServices.addAll(remoteStatus.services)
                remoteGroups.addAll(remoteStatus.groups?.list ?: emptyList())
            }

            is RemoteStatus.Error -> {
                if (remoteStatus.type == RemoteBackupErrorType.FILE_NOT_FOUND) {
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
            val matchingLocalGroup = localGroups.list.find { it.id == remoteGroup.id }
            compareLocalGroupsWithRemote(
                matchingLocalGroup,
                remoteGroup,
                localRevision,
                remoteRevision
            )
            localGroups.list.remove(matchingLocalGroup)
        }

        // Sync matching services
        remoteServices.forEach { remoteDto ->
            val matchingLocalDto = localServices.find {
                it.secret.lowercase(Locale.US) == remoteDto.secret.lowercase(Locale.US)
            }
            compareLocalServiceWithRemote(
                matchingLocalDto,
                remoteDto,
                localRevision,
                remoteRevision
            )
            localServices.remove(matchingLocalDto)
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
                trashService.execute(TrashService.Params(it, false)).blockingGet()
            }
        }

        // Manage groups which are present on local but missing from remote
        localGroups.list.forEach {
            // Remove local group if group is missing from remote and was SYNCED before
            if (
                isFirstConnect.not()
                && localAppVersionCode == remoteAppVersionCode
                && localSchemaVersion == remoteSchemaVersion
                && it.backupSyncStatus == BackupSyncStatus.SYNCED
            ) {
                storeGroups.delete(it)
            }
        }

        if (storeGroups.all().list.all { it.updatedAt < localRevision }) {
            storeGroups.sortById(remoteGroups.map { it.id!! })
        }

        return updateRemote(now, isFirstConnect, password, trigger)
    }

    private fun updateRemote(
        now: Long,
        isFirstConnect: Boolean,
        password: String?,
        trigger: SyncBackupTrigger
    ): RemoteStatus {
        try {
            val updatedLocalServices = getLocalServices()
            val updateResult = updateRemoteBackup.execute(
                UpdateRemoteBackup.Params(
                    isFirstConnect = isFirstConnect,
                    updatedAt = now,
                    password = if (trigger == SyncBackupTrigger.RemovePassword) null else password,
                    remoteBackupKey = if (trigger == SyncBackupTrigger.RemovePassword) null else remoteBackupKeyPreference.get(),
                )
            ).blockingGet()

            when (updateResult) {
                is UpdateRemoteBackupResult.Success -> {
                    // Doesn't work as expected - commented out for now
//                    // Update order on first connect
//                    if (removeIfNotPresentOnRemote.not()) {
//                        storeServicesOrder.saveOrder(
//                                ServicesOrder(
//                                        updatedLocalServices.sortedBy { localService ->
//                                            (remoteStatus as RemoteStatus.Success).services.map { it.secret }.indexOf(localService.secret)
//                                        }.map { it.id }
//                                )
//                        )
//                    }

                    // Mark services as SYNCED
                    servicesRepository.updateService(
                        *updatedLocalServices
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
                            .toTypedArray()
                    ).blockingGet()

                    // Mark groups as SYNCED
                    storeGroups.markAllSynced()

                    return RemoteStatus.Success(
                        emptyList(),
                        null,
                        0,
                        RemoteBackup.CURRENT_SCHEMA,
                        0
                    )
                }

                is UpdateRemoteBackupResult.Failure -> {
                    return RemoteStatus.Error(
                        Operation.UPDATE,
                        updateResult.type,
                        updateResult.throwable
                    )
                }
            }
        } catch (e: Exception) {
            return RemoteStatus.Error(Operation.SYNC, RemoteBackupErrorType.SYNC_FAILURE, e)
        }
    }

    private fun compareLocalServiceWithRemote(
        local: ServiceDto?,
        remote: ServiceDto,
        localRevision: Long,
        remoteRevision: Long
    ) {
        // Service exists on remote, but not on local
        if (local == null) {
            // Service was not recently deleted, add it to the list
            if (storeRecentlyDeleted.get(remote.secret) == null) {
                addService.execute(
                    AddService.Params(
                        remote,
                        trigger = AddService.Trigger.ADD_FROM_BACKUP
                    )
                ).blockingGet()
                return
            }

            // Service was recently deleted
            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                // Revisions are different, for safety reasons restore the service from remote
                addService.execute(
                    AddService.Params(
                        remote.copy(),
                        trigger = AddService.Trigger.ADD_FROM_BACKUP
                    )
                ).blockingGet()
                storeRecentlyDeleted.remove(remote.secret)
                return
            }

            // Service was recently deleted
            // Revisions are the same, remove service from cloud
            doNothing()
            storeRecentlyDeleted.remove(remote.secret)
            return
        }

        // Services exists on local and remote
        // Check their edit time and update if needed -> override with remote
        if (remote.updatedAt > local.updatedAt) {
            editService.execute(
                remote.copy(
                    id = local.id,
                    assignedDomains = local.assignedDomains,
                )
            ).blockingGet()
        }
    }

    private fun compareLocalGroupsWithRemote(
        local: Group?,
        remote: Group,
        localRevision: Long,
        remoteRevision: Long
    ) {
        // Group exists on remote, but not on local
        if (local == null) {

            // Check if database revisions are the same on local and on remote
            if (localRevision != remoteRevision) {
                storeGroups.add(remote)
            }

            // Group was locally deleted
            // Revisions are the same, remove service from cloud
            doNothing()
            return
        }

        // Groups exists on local and remote
        // Check their edit time and update if needed ->  override with remote
        if (local.isContentEqualTo(remote).not() && remote.updatedAt > local.updatedAt) {
            storeGroups.edit(remote)
        }
    }

    private fun getLocalServices() =
        getServices.execute().blockingGet()

    private fun getRemoteBackup(password: String?): RemoteStatus {
        val result = getRemoteBackup.execute(GetRemoteBackup.Params(password)).blockingGet()

        return when (result) {
            is GetRemoteBackupResult.Success -> RemoteStatus.Success(
                services = result.backup.services.sortedBy { it.order.position }.map {
                    val serviceTypeIdFromLegacy = it.type?.name?.let { type ->
                        LegacyTypeToId.serviceIds.getOrDefault(
                            type,
                            null
                        )
                    }
                    var iconCollectionIdFromLegacy =
                        ServiceIcons.getIconCollection(serviceTypeIdFromLegacy.orEmpty())
                    val brandId = it.icon?.brand?.id?.name
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
                            LegacyTypeToId.manualNames.getOrDefault(it.name.lowercase(), null)
                        if (serviceTypeIdForManualName.isNullOrBlank().not()) {
                            iconCollectionIdFromLegacy =
                                ServiceIcons.getIconCollection(serviceTypeIdForManualName!!)
                        }
                    }

                    ServiceDto.fromRemote(
                        remoteService = it,
                        serviceTypeIdFromLegacy = serviceTypeIdFromLegacy,
                        iconCollectionIdFromLegacy = iconCollectionIdFromLegacy
                    )

                },
                groups = if (result.backup.groups.isEmpty()) {
                    null
                } else {
                    Groups(result.backup.groups.map { it.toLocal() }.toMutableList())
                },
                lastSyncTime = result.backup.updatedAt,
                schemaVersion = result.backup.schemaVersion,
                appVersionCode = result.backup.appVersionCode,
            )

            is GetRemoteBackupResult.Failure -> RemoteStatus.Error(
                Operation.GET,
                result.type,
                result.throwable
            )
        }
    }
}