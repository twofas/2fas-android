package com.twofasapp.services.domain

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.common.domain.WidgetCallbacks
import io.reactivex.Completable
import io.reactivex.Scheduler

internal class EditServiceUseCaseImpl(
    private val servicesRepository: ServicesRepository,
    private val timeProvider: TimeProvider,
    private val widgetCallbacks: WidgetCallbacks,
) : EditServiceUseCase {

    override fun execute(params: ServiceDto, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        val newService = params.copy(
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
            updatedAt = timeProvider.systemCurrentTime()
        )

        widgetCallbacks.onServiceChanged()

        return servicesRepository.updateService(newService)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}
