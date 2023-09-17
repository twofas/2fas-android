package com.twofasapp.usecases.services

import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.widgets.adapter.WidgetViewsData
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import io.reactivex.Completable
import io.reactivex.Scheduler

class TrashService(
    private val servicesRepository: ServicesRepository,
    private val storeServicesOrder: StoreServicesOrder,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val widgetViewsData: WidgetViewsData,
    private val widgetBroadcaster: WidgetBroadcaster,
    private val storeRecentlyDeleted: StoreRecentlyDeleted,
    private val timeProvider: TimeProvider,
    private val remoteBackupStatusPreference: com.twofasapp.prefs.usecase.RemoteBackupStatusPreference,
) : UseCaseParameterized<TrashService.Params, Completable> {

    data class Params(
        val service: ServiceDto,
        val shouldTriggerSync: Boolean = true,
    )

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        val newService = params.service.copy(
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
            updatedAt = timeProvider.systemCurrentTime(),
            isDeleted = true,
        )

        return servicesRepository.updateService(newService)
            .andThen(deleteFromServicesOrder(newService.id))
            .doOnComplete {
                storeServicesOrder.deleteFromOrder(newService.id)
            }
            .doOnComplete {
                widgetViewsData.invalidateCache()
                widgetBroadcaster.sendServiceChanged()
            }
            .doOnComplete {
                if (remoteBackupStatusPreference.get().state == com.twofasapp.prefs.model.RemoteBackupStatusEntity.State.ACTIVE && params.shouldTriggerSync) {
                    storeRecentlyDeleted.add(
                        com.twofasapp.prefs.model.RecentlyDeletedService(
                            newService.secret,
                            timeProvider.systemCurrentTime()
                        )
                    )
                    syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED)
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun deleteFromServicesOrder(id: Long) =
        Completable.fromCallable { storeServicesOrder.deleteFromOrder(id) }
}
