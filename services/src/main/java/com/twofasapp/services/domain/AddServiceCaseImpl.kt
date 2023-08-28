package com.twofasapp.services.domain

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.prefs.usecase.FirstCodeAddedPreference
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.model.Service

internal class AddServiceCaseImpl(
    private val repository: ServicesRepository,
    private val timeProvider: TimeProvider,
    private val storeHotpServices: StoreHotpServices,
    private val firstCodeAddedPreference: FirstCodeAddedPreference,
    private val showBackupNotice: ShowBackupNotice,
) : AddServiceCase {

    override suspend fun invoke(service: Service, trigger: AddServiceCase.Trigger): Long {
        val serviceId = repository.insertService(
            service.copy(
                id = 0L,
                backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                updatedAt = timeProvider.systemCurrentTime(),
            )
        )

        // Add to order
        with(repository.getServicesOrder()) {
            repository.updateServicesOrder(servicesOrder = copy(ids = ids.plus(serviceId)))
        }

        // Send icon analytics
        if (service.serviceTypeId.isNullOrBlank()) {
        }

        // Store HOTP
        if (service.authType == Service.AuthType.HOTP && trigger != AddServiceCase.Trigger.FromBackup) {
            storeHotpServices.onServiceAdded(service.secret)
        }

        // Capture analytics
        if (trigger != AddServiceCase.Trigger.FromBackup) {
        }

        // Handle first code
        if (firstCodeAddedPreference.get().not()) {
            firstCodeAddedPreference.put(true)
            showBackupNotice.save(true)
        }

        return serviceId
    }
}
