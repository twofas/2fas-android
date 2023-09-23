package com.twofasapp.services.data

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.ServicesOrder
import com.twofasapp.services.domain.model.Service
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun select(): Single<List<ServiceDto>>
    fun insertService(service: ServiceDto): Single<Long>
    fun updateService(vararg services: ServiceDto): Completable
    fun deleteService(vararg services: ServiceDto): Completable

    suspend fun insertService(service: Service): Long
    fun selectFlow(): Flow<List<Service>>
    fun observe(serviceId: Long): Flow<Service>
    suspend fun getServicesOrder(): ServicesOrder
    suspend fun updateServicesOrder(servicesOrder: ServicesOrder)
    suspend fun assignServiceDomain(service: Service, domain: String)
    suspend fun updateService(vararg services: Service)
}