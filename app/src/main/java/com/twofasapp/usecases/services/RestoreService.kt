package com.twofasapp.usecases.services

import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.time.domain.TimeProvider
import com.twofasapp.widgets.adapter.WidgetViewsData
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import io.reactivex.Completable
import io.reactivex.Scheduler

class RestoreService(
    private val servicesRepository: ServicesRepository,
    private val storeServicesOrder: StoreServicesOrder,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val widgetViewsData: WidgetViewsData,
    private val widgetBroadcaster: WidgetBroadcaster,
    private val timeProvider: TimeProvider,
    private val remoteBackupStatusPreference: com.twofasapp.prefs.usecase.RemoteBackupStatusPreference,
) : UseCaseParameterized<ServiceDto, Completable> {

    override fun execute(params: ServiceDto, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        val newService = params.copy(
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
            updatedAt = timeProvider.systemCurrentTime(),
            isDeleted = false,
        )

        return servicesRepository.updateService(newService)
            .andThen(addToServicesOrder(newService.id))
            .doOnComplete {
                storeServicesOrder.addToOrder(newService.id)
            }
            .doOnComplete {
                widgetViewsData.invalidateCache()
                widgetBroadcaster.sendServiceChanged()
            }
            .doOnComplete {
                if (remoteBackupStatusPreference.get().state == com.twofasapp.prefs.model.RemoteBackupStatus.State.ACTIVE) {
                    syncBackupDispatcher.dispatch(SyncBackupTrigger.SERVICES_CHANGED)
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun addToServicesOrder(id: Long) =
        Completable.fromCallable { storeServicesOrder.addToOrder(id) }
}
