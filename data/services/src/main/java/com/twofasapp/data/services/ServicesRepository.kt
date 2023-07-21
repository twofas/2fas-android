package com.twofasapp.data.services

import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.services.domain.Service
import com.twofasapp.parsers.domain.OtpAuthLink
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun observeServices(): Flow<List<Service>>
    fun observeServicesTicker(): Flow<List<Service>>
    fun observeDeletedServices(): Flow<List<Service>>
    fun observeService(id: Long): Flow<Service>
    fun observeRecentlyAddedService(): Flow<RecentlyAddedService>
    fun setTickerEnabled(enabled: Boolean)
    suspend fun getServices(): List<Service>
    suspend fun getService(id: Long): Service
    suspend fun deleteService(id: Long)
    suspend fun updateService(service: Service)
    suspend fun setServiceGroup(id: Long, groupId: String?)
    suspend fun trashService(id: Long)
    suspend fun restoreService(id: Long)
    fun updateServicesOrder(ids: List<Long>)
    suspend fun incrementHotpCounter(service: Service)
    fun pushRecentlyAddedService(recentlyAddedService: RecentlyAddedService)
    suspend fun recalculateTimeDelta()
    suspend fun isServiceExists(secret: String): Boolean
    fun isSecretValid(secret: String): Boolean
    fun isServiceValid(link: OtpAuthLink): Boolean
    suspend fun addService(link: OtpAuthLink): Long
    suspend fun addService(service: Service): Long
    fun observeAddServiceAdvancedExpanded(): Flow<Boolean>
    fun pushAddServiceAdvancedExpanded(expanded: Boolean)
    suspend fun revealService(id: Long)
}