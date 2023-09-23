package com.twofasapp.data.services

import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.RecentlyDeleted
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun observeServices(): Flow<List<Service>>
    fun observeServicesWithCode(): Flow<List<Service>>
    fun observeServicesTicker(): Flow<List<Service>>
    fun observeDeletedServices(): Flow<List<Service>>
    fun observeRecentlyAddedService(): Flow<RecentlyAddedService>
    fun setTickerEnabled(enabled: Boolean)
    suspend fun getServices(): List<Service>
    suspend fun getServicesIncludingDeleted(): List<Service>
    suspend fun getService(id: Long): Service
    suspend fun deleteService(id: Long)
    suspend fun updateService(service: Service)
    suspend fun updateServicesFromCloud(services: List<Service>)
    suspend fun setServiceGroup(id: Long, groupId: String?)
    suspend fun trashService(id: Long, triggerSync: Boolean = true)
    suspend fun restoreService(id: Long)
    fun updateServicesOrder(ids: List<Long>)
    suspend fun incrementHotpCounter(service: Service)
    fun pushRecentlyAddedService(recentlyAddedService: RecentlyAddedService)
    suspend fun recalculateTimeDelta()
    suspend fun isServiceExists(secret: String): Boolean
    fun isSecretValid(secret: String): Boolean
    fun isServiceValid(link: OtpAuthLink): Boolean
    suspend fun addService(link: OtpAuthLink): Long
    suspend fun addService(service: Service, triggerSync: Boolean = true): Long
    suspend fun addServices(services: List<Service>)
    fun observeAddServiceAdvancedExpanded(): Flow<Boolean>
    fun pushAddServiceAdvancedExpanded(expanded: Boolean)
    fun setManualGuideSelectedPrefill(prefill: String?)
    fun getManualGuideSelectedPrefill(): String?
    suspend fun revealService(id: Long)
    suspend fun getRecentlyDeletedServices(): RecentlyDeleted
    suspend fun removeRecentlyDeleted(secret: String)
    suspend fun assignDomainToService(service: Service, domain: String)
}