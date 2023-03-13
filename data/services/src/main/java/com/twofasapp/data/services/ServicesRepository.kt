package com.twofasapp.data.services

import com.twofasapp.data.services.domain.Service
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun observeServices(): Flow<List<Service>>
    fun observeServicesTicker(): Flow<List<Service>>
    fun observeDeletedServices(): Flow<List<Service>>
    fun observeService(id: Long): Flow<Service>
    fun observeRecentlyAddedService(): Flow<Service>
    suspend fun getServices(): List<Service>
    suspend fun getService(id: Long): Service
    suspend fun deleteService(id: Long)
    suspend fun trashService(id: Long)
    suspend fun restoreService(id: Long)
    suspend fun swapServices(from: Long, to: Long)
    fun pushRecentlyAddedService(id: Long)
}