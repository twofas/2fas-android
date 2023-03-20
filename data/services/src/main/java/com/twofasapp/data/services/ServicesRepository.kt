package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Service
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun observeServices(): Flow<List<Service>>
    fun observeServicesTicker(): Flow<List<Service>>
    fun observeDeletedServices(): Flow<List<Service>>
    fun observeService(id: Long): Flow<Service>
    fun observeRecentlyAddedService(): Flow<Service>
    fun setTickerEnabled(enabled: Boolean)
    suspend fun getServices(): List<Service>
    suspend fun getService(id: Long): Service
    suspend fun deleteService(id: Long)
    suspend fun setServiceGroup(id: Long, groupId: String?)
    suspend fun trashService(id: Long)
    suspend fun restoreService(id: Long)
    fun updateServicesOrder(ids: List<Long>)
    suspend fun incrementHotpCounter(service: Service)
    fun pushRecentlyAddedService(id: Long)
}