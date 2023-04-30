package com.twofasapp.services.domain

import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.widgets.domain.WidgetActions

internal class MoveToTrashCaseImpl(
    private val repository: ServicesRepository,
    private val timeProvider: TimeProvider,
    private val widgetActions: WidgetActions,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) : MoveToTrashCase {

    override suspend fun invoke(serviceId: Long, triggerSync: Boolean) {
        val service = repository.select(serviceId).copy(
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
            updatedAt = timeProvider.systemCurrentTime(),
            isDeleted = true,
        )

        // Update service
        repository.updateService(service)

        // Remove from services order
        with(repository.getServicesOrder()) {
            repository.updateServicesOrder(
                copy(ids = ids.minus(serviceId))
            )
        }

        // Notify widgets
        widgetActions.onServiceChanged()

        // Sync backup if active and add service to recently deleted
        if (triggerSync && remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.ACTIVE) {
            repository.addToRecentlyDeleted(service.secret)
            syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED)
        }
    }
}