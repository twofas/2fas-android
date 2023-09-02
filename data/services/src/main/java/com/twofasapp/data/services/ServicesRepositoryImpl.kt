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
import com.twofasapp.data.services.otp.ServiceParser
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.RecentlyDeletedService
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.RecentlyDeletedPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.time.domain.RecalculateTimeDeltaCase
import com.twofasapp.widgets.domain.WidgetActions
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
    private val widgetActions: WidgetActions,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
    private val recentlyDeletedPreference: RecentlyDeletedPreference,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val recalculateTimeDeltaCase: RecalculateTimeDeltaCase,
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

    override suspend fun updateService(service: Service) {
        withContext(dispatchers.io) {
            local.updateService(
                service.copy(
                    backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
                    updatedAt = timeProvider.systemCurrentTime(),
                )
            )

            widgetActions.onServiceChanged()
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

    override fun pushRecentlyAddedService(recentlyAddedService: RecentlyAddedService) {
        local.pushRecentlyAddedService(recentlyAddedService)
    }

    override suspend fun recalculateTimeDelta() {
        recalculateTimeDeltaCase.invoke()
    }

    override suspend fun isServiceExists(secret: String): Boolean {
        return getServices()
            .filter { it.isDeleted.not() }
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

    override suspend fun addService(service: Service): Long {
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

            syncBackupDispatcher.tryDispatch(SyncBackupTrigger.SERVICES_CHANGED)

            id
        }
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
}