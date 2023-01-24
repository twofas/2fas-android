package com.twofasapp.data.services

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.tickerFlow
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.local.ServicesLocalSource
import com.twofasapp.data.services.otp.ServiceCodeGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ServicesRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val codeGenerator: ServiceCodeGenerator,
    private val localServices: ServicesLocalSource,
) : ServicesRepository {

    override fun observeServices(): Flow<List<Service>> {
        return localServices.observeServices()
    }

    override fun observeServicesTicker(): Flow<List<Service>> {
        return combine(
            tickerFlow(1000L),
            observeServices(),
        ) { a, b -> b }
            .map { services ->
                services.map { codeGenerator.generate(it) }
            }
    }

    override fun observeDeletedServices(): Flow<List<Service>> {
        return localServices.observeDeletedServices()
    }

    override fun observeService(id: Long): Flow<Service> {
        return localServices.observeService(id)
    }

    override suspend fun getServices(): List<Service> {
        return withContext(dispatchers.io) {
            localServices.getServices()
        }
    }

    override suspend fun getService(id: Long): Service {
        return withContext(dispatchers.io) {
            localServices.getService(id)

        }
    }

    override suspend fun deleteService(id: Long) {
        withContext(dispatchers.io) {
            localServices.deleteService(id)
        }
    }

    override suspend fun trashService(id: Long) {
        withContext(dispatchers.io) {
            localServices.updateService(
                localServices.getService(id).copy(
                    // TODO: see TrashService.kt
                )
            )
        }
    }

    override suspend fun restoreService(id: Long) {
        withContext(dispatchers.io) {
            localServices.updateService(
                localServices.getService(id).copy(
                    // TODO: see RestoreService.kt
                )
            )
        }
    }
}