package com.twofasapp.data.services.local

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.domain.ServicesOrder
import com.twofasapp.data.services.local.model.ServicesOrderEntity
import com.twofasapp.data.services.mapper.asDomain
import com.twofasapp.data.services.mapper.asEntity
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

internal class ServicesLocalSource(
    private val json: Json,
    private val preferences: PlainPreferences,
    private val dao: ServiceDao,
    private val timeProvider: TimeProvider,
) {
    companion object {
        private const val KeyOrder = "servicesOrder"
    }

    private val recentlyAddedServiceFlow: MutableSharedFlow<RecentlyAddedService> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val addServiceAdvancedExpanded: MutableSharedFlow<Boolean> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private fun log(msg: String) {
        Timber.tag("ServicesDao").i(msg)
    }

    fun observeServices(): Flow<List<Service>> {
        return dao.observe().map { list ->
            list.filter { it.isDeleted != true }.map { it.asDomain() }
        }
    }

    fun observeDeletedServices(): Flow<List<Service>> {
        return dao.observe().map { list ->
            list.filter { it.isDeleted == true }.map { it.asDomain() }
        }
    }

    suspend fun getServices(): List<Service> {
        return dao.select().map { it.asDomain() }
    }

    fun observeService(id: Long): Flow<Service> {
        return dao.observe(id).map { it.asDomain() }
    }

    fun observeRecentlyAddedService(): Flow<RecentlyAddedService> {
        return recentlyAddedServiceFlow.filterNotNull()
    }

    suspend fun insertService(service: Service): Long {
        return dao.insert(service.asEntity())
    }

    suspend fun getService(id: Long): Service {
        return dao.select(id).asDomain()
    }

    suspend fun getServiceBySecret(secret: String): Service? {
        return dao.selectBySecret(secret)?.asDomain()
    }

    suspend fun deleteService(id: Long) {
        log("Delete service $id")
        dao.delete(id)
    }

    suspend fun deleteService(secret: String) {
        log("Delete service")
        return dao.deleteBySecret(secret)
    }

    suspend fun updateService(service: Service) {
        dao.update(service.asEntity())
    }

    private fun getOrder(): ServicesOrderEntity {
        return preferences.getString(KeyOrder)?.let {
            json.decodeFromString(it)
        } ?: ServicesOrderEntity()
    }

    private fun saveOrder(entity: ServicesOrderEntity) {
        preferences.putString(KeyOrder, json.encodeToString(entity))
    }

    fun observeOrder(): Flow<ServicesOrder> {
        return preferences.observe(KeyOrder, "").map { value ->
            (value?.let {
                try {
                    json.decodeFromString(value)
                } catch (e: Exception) {
                    ServicesOrderEntity()
                }
            } ?: ServicesOrderEntity()).asDomain()
        }
    }

    fun deleteServiceFromOrder(id: Long) {
        val local = getOrder()
        val newOrder = local.copy(
            ids = local.ids.minus(id)
        )

        saveOrder(newOrder)
    }

    fun addServiceToOrder(id: Long) {
        val local = getOrder()
        val newOrder = local.copy(
            ids = local.ids.plus(id)
        )
        saveOrder(newOrder)
    }

    fun pushRecentlyAddedService(recentlyAddedService: RecentlyAddedService) {
        GlobalScope.launch {
            recentlyAddedServiceFlow.emit(recentlyAddedService)
        }
    }

    suspend fun incrementHotpCounter(id: Long, counter: Int, timestamp: Long) {
        dao.update(
            dao.select(id).copy(
                hotpCounter = counter,
                hotpCounterTimestamp = timestamp,
                backupSyncStatus = BackupSyncStatus.NOT_SYNCED.name,
                updatedAt = timeProvider.systemCurrentTime(),
            )
        )
    }

    suspend fun setServiceGroup(id: Long, groupId: String?) {
        dao.update(
            dao.select(id).copy(
                groupId = groupId,
                backupSyncStatus = BackupSyncStatus.NOT_SYNCED.name,
                updatedAt = timeProvider.systemCurrentTime(),
            )
        )
    }

    fun saveServicesOrder(ids: List<Long>) {
        val local = getOrder()
        saveOrder(local.copy(ids = ids))
    }

    suspend fun cleanUpGroups(groupIds: List<String>) {
        dao.cleanUpGroups(groupIds)
    }

    fun observeAddServiceAdvancedExpanded(): Flow<Boolean> {
        return addServiceAdvancedExpanded
    }

    fun pushAddServiceAdvancedExpanded(expanded: Boolean) {
        addServiceAdvancedExpanded.tryEmit(expanded)
    }

    suspend fun revealService(id: Long) {
        dao.update(
            dao.select(id).copy(
                revealTimestamp = System.currentTimeMillis(),
            )
        )
    }
}