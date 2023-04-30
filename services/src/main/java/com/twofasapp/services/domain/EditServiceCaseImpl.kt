package com.twofasapp.services.domain

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.model.Service
import com.twofasapp.widgets.domain.WidgetActions

internal class EditServiceCaseImpl(
    private val servicesRepository: ServicesRepository,
    private val timeProvider: TimeProvider,
    private val widgetActions: WidgetActions,
) : EditServiceCase {

    override suspend fun invoke(service: Service) {
        return servicesRepository.updateService(
            service.copy(
                backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                updatedAt = timeProvider.systemCurrentTime(),
            )
        ).also {
            widgetActions.onServiceChanged()
        }
    }
}