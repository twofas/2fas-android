package com.twofasapp.data.services

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.common.domain.Service
import com.twofasapp.common.domain.WidgetCallbacks
import com.twofasapp.common.ktx.tickerFlow
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.otp.ServiceCodeGenerator
import com.twofasapp.data.services.otp.ServiceParser
import com.twofasapp.data.services.remote.CloudSyncWorkDispatcher
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.RecentlyDeleted
import com.twofasapp.prefs.model.RecentlyDeletedService
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.prefs.usecase.RecentlyDeletedPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class ServicesRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val timeProvider: TimeProvider,
    private val codeGenerator: ServiceCodeGenerator,
    private val local: ServicesLocalSource,
    private val widgetCallbacks: WidgetCallbacks,
    private val cloudSyncWorkDispatcher: CloudSyncWorkDispatcher,
    private val recentlyDeletedPreference: RecentlyDeletedPreference,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) : ServicesRepository {

    private val isTickerEnabled = MutableStateFlow(true)
    private var guideManualPrefill: String? = null

    override fun observeServices(): Flow<List<Service>> {
        return combine(
            local.observeServices(),
            local.observeOrder(),
        ) { services, order ->
            services.sortedBy { order.ids.indexOf(it.id) }
        }
    }

    override fun observeServicesWithCode(): Flow<List<Service>> {
        return observeServices().map { services ->
            services.map { codeGenerator.generate(it) }
        }
    }

    override fun observeServicesTicker(): Flow<List<Service>> {
        return combine(
            isTickerEnabled,
            tickerFlow(1_000L),
            observeServices(),
        ) { a, b, c -> Pair(a, c) }
            .filter { it.first } // ticker enabled
            .map { (_, services) ->
                services.map { codeGenerator.generate(it) }
            }
    }

    override fun observeDeletedServices(): Flow<List<Service>> {
        return local.observeDeletedServices()
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

    override suspend fun getServicesIncludingDeleted(): List<Service> {
        return withContext(dispatchers.io) {
            local.getServicesIncludingDeleted()
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

            if (remoteBackupStatusPreference.get().state == RemoteBackupStatusEntity.State.ACTIVE) {
                cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.ServicesChanged)
            }
        }
    }

    override suspend fun updateService(service: Service) {
        withContext(dispatchers.io) {
            local.updateService(
                service.copy(
                    backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    updatedAt = timeProvider.systemCurrentTime(),
                )
            )

            widgetCallbacks.onServiceChanged()
        }
    }

    override suspend fun updateServicesFromCloud(services: List<Service>) {
        // Method used only in cloud sync -> do not change sync status and updatedAt
        // Refactor at some point
        withContext(dispatchers.io) {
            services.forEach {
                local.updateService(it)
            }

            widgetCallbacks.onServiceChanged()

        }
    }

    override suspend fun setServiceGroup(id: Long, groupId: String?) {
        withContext(dispatchers.io) {
            local.setServiceGroup(id, groupId)
        }
    }

    override suspend fun trashService(id: Long, triggerSync: Boolean) {
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
            widgetCallbacks.onServiceDeleted(id)

            if (remoteBackupStatusPreference.get().state == RemoteBackupStatusEntity.State.ACTIVE) {
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

                if (triggerSync) {
                    cloudSyncWorkDispatcher.tryDispatch(trigger = CloudSyncTrigger.ServicesChanged)
                }
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
            widgetCallbacks.onServiceChanged()

            if (remoteBackupStatusPreference.get().state == RemoteBackupStatusEntity.State.ACTIVE) {
                cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.ServicesChanged)
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

    override fun pushRecentlyAddedService(recentlyAddedService: RecentlyAddedService) {
        local.pushRecentlyAddedService(recentlyAddedService)
    }

    override suspend fun isServiceExists(secret: String): Boolean {
        return getServices()
            .map { it.secret.lowercase() }
            .contains(secret.lowercase())
    }

    override fun isSecretValid(secret: String): Boolean {
        return codeGenerator.check(secret)
    }

    override fun isServiceValid(link: OtpAuthLink): Boolean {
        return try {
            val otpAlgorithm = link.params[OtpAuthLink.ALGORITHM_PARAM]
            val otpDigits = link.params[OtpAuthLink.DIGITS_PARAM]?.toIntOrNull()
            val otpPeriod = link.params[OtpAuthLink.PERIOD_PARAM]?.toIntOrNull()

            val algorithm = when {
                otpAlgorithm == null -> Service.Algorithm.SHA1
                otpAlgorithm.equals("SHA1", ignoreCase = true) -> Service.Algorithm.SHA1
                otpAlgorithm.equals("SHA224", ignoreCase = true) -> Service.Algorithm.SHA224
                otpAlgorithm.equals("SHA256", ignoreCase = true) -> Service.Algorithm.SHA256
                otpAlgorithm.equals("SHA384", ignoreCase = true) -> Service.Algorithm.SHA384
                otpAlgorithm.equals("SHA512", ignoreCase = true) -> Service.Algorithm.SHA512
                else -> return false
            }

            val digits = when {
                otpDigits == 6 -> 6
                otpDigits == 7 -> 7
                otpDigits == 8 -> 8
                otpDigits == null -> 6
                else -> return false
            }

            val period = when {
                otpPeriod == 30 -> 30
                otpPeriod == 60 -> 60
                otpPeriod == 90 -> 90
                otpPeriod == null -> 30
                else -> return false
            }

            codeGenerator.check(
                secret = link.secret,
                digits = digits,
                period = period,
                algorithm = algorithm
            )
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun addService(link: OtpAuthLink): Long {
        return withContext(dispatchers.io) {
            val service = ServiceParser.parseService(link)

            addService(service)
        }
    }

    override suspend fun addService(service: Service, triggerSync: Boolean): Long {
        return withContext(dispatchers.io) {
            // Delete duplicate, if any
            val existingService = local.getServiceBySecret(service.secret)
            existingService?.let {
                local.deleteService(it.secret)
                local.deleteServiceFromOrder(it.id)
            }

            // Insert
            val id = local.insertService(service)
            local.addServiceToOrder(id)

            if (triggerSync) {
                cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.ServicesChanged)
            }

            id
        }
    }

    override suspend fun addServices(services: List<Service>) {
        services.forEach { service ->
            addService(service, false)
        }

        cloudSyncWorkDispatcher.tryDispatch(CloudSyncTrigger.ServicesChanged)
    }

    override fun observeAddServiceAdvancedExpanded(): Flow<Boolean> {
        return local.observeAddServiceAdvancedExpanded()
    }

    override fun pushAddServiceAdvancedExpanded(expanded: Boolean) {
        local.pushAddServiceAdvancedExpanded(expanded)
    }

    override suspend fun revealService(id: Long) {
        withContext(dispatchers.io) {
            local.revealService(id)
        }
    }

    override fun setManualGuideSelectedPrefill(prefill: String?) {
        guideManualPrefill = prefill
    }

    override fun getManualGuideSelectedPrefill(): String? {
        return guideManualPrefill
    }

    override suspend fun getRecentlyDeletedServices(): RecentlyDeleted {
        return recentlyDeletedPreference.get()
    }

    override suspend fun removeRecentlyDeleted(secret: String) {
        getRecentlyDeletedServices().let {
            val index = it.services.indexOfFirst { service -> service.secret == secret }

            if (index > -1) {
                recentlyDeletedPreference.put(it.copy(services = it.services.filter { service -> service.secret != secret }))
            }
        }
    }

    override suspend fun assignDomainToService(service: Service, domain: String) {
        if (service.assignedDomains.contains(domain.lowercase())) {
            return
        }

        // Remove duplicates
        val matchingServices = local.getServicesIncludingDeleted()
            .filter { it.assignedDomains.contains(domain.lowercase()) }

        matchingServices.forEach { matched ->
            updateService(
                matched.copy(
                    assignedDomains = matched.assignedDomains.minus(domain.lowercase()),
                    updatedAt = timeProvider.systemCurrentTime(),
                )
            )
        }

        updateService(
            service.copy(
                assignedDomains = service.assignedDomains.plus(domain.lowercase()),
                updatedAt = timeProvider.systemCurrentTime(),
            )
        )
    }
}