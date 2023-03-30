package com.twofasapp.data.services

import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.tickerFlow
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.otp.ServiceCodeGenerator
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.RecentlyDeletedService
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.RecentlyDeletedPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.widgets.domain.WidgetActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ServicesRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val timeProvider: TimeProvider,
    private val codeGenerator: ServiceCodeGenerator,
    private val local: ServicesLocalSource,
    private val widgetActions: WidgetActions,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val recentlyDeletedPreference: RecentlyDeletedPreference,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) : ServicesRepository {

    private val isTickerEnabled = MutableStateFlow(true)

    override fun observeServices(): Flow<List<Service>> {
        return combine(
            local.observeServices(),
            local.observeOrder(),
        ) { services, order ->
            services.sortedBy { order.ids.indexOf(it.id) }
        }
    }

    override fun observeServicesTicker(): Flow<List<Service>> {
        return combine(
            isTickerEnabled,
            tickerFlow(1000L),
            observeServices(),
        ) { a, b, c -> Pair(a, c) }
//            .filter { it.first } // TODO: ticker
            .map { (_, services) ->
                services.map { codeGenerator.generate(it) }
            }
    }

    override fun observeDeletedServices(): Flow<List<Service>> {
        return local.observeDeletedServices()
    }

    override fun observeService(id: Long): Flow<Service> {
        return local.observeService(id)
    }

    override fun observeRecentlyAddedService(): Flow<RecentlyAddedService> {
        return local.observeRecentlyAddedService()
    }

    override fun setTickerEnabled(enabled: Boolean) {
        isTickerEnabled.tryEmit(enabled)
    }

    override suspend fun getServices(): List<Service> {
        return withContext(dispatchers.io) {
            local.getServices()
        }
    }

    override suspend fun getService(id: Long): Service {
        return withContext(dispatchers.io) {
            local.getService(id)

        }
    }

    override suspend fun deleteService(id: Long) {
        withContext(dispatchers.io) {
            local.deleteService(id)

            if (remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE) {
                syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED)
            }
        }
    }

    override suspend fun setServiceGroup(id: Long, groupId: String?) {
        withContext(dispatchers.io) {
            local.setServiceGroup(id, groupId)
        }
    }

    override suspend fun trashService(id: Long) {
        // See TrashService.kt
        withContext(dispatchers.io) {
            val localService = local.getService(id)

            local.updateService(
                localService.copy(
                    backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    updatedAt = timeProvider.systemCurrentTime(),
                    isDeleted = true,
                )
            )

            local.deleteServiceFromOrder(id)
            widgetActions.onServiceDeleted(id)


            if (remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE) {
                val recentlyDeleted = recentlyDeletedPreference.get()
                recentlyDeletedPreference.put(
                    recentlyDeleted.copy(
                        services = recentlyDeleted.services.plus(
                            RecentlyDeletedService(
                                secret = localService.secret,
                                deletedAt = timeProvider.systemCurrentTime()
                            )
                        )
                    )
                )

                syncBackupDispatcher.tryDispatch(trigger = SyncBackupTrigger.SERVICES_CHANGED)
            }
        }
    }

    override suspend fun restoreService(id: Long) {
        // See RestoreService.kt

        withContext(dispatchers.io) {
            val localService = local.getService(id)

            local.updateService(
                localService.copy(
                    backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    updatedAt = timeProvider.systemCurrentTime(),
                    isDeleted = false,
                )
            )

            local.addServiceToOrder(id)
            widgetActions.onServiceChanged()

            if (remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE) {
                syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED)
            }
        }
    }

    override fun updateServicesOrder(ids: List<Long>) {
        local.saveServicesOrder(ids)
    }

    override suspend fun incrementHotpCounter(service: Service) {
        withContext(dispatchers.io) {
            local.incrementHotpCounter(
                id = service.id,
                counter = (service.hotpCounter ?: 1) + 1,
                timestamp = timeProvider.systemCurrentTime(),
            )
        }
    }

    override fun pushRecentlyAddedService(id: Long, source: RecentlyAddedService.Source) {
        local.pushRecentlyAddedService(id, source)
    }
}