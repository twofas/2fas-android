package com.twofasapp.services.data

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.domain.model.Service
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

internal interface ServicesLocalData {
    fun select(): Single<List<ServiceDto>>
    suspend fun selectAll(): List<Service>
    fun selectFlow(): Flow<List<Service>>
    fun observe(serviceId: Long): Flow<Service>
    fun observe(): Flowable<List<ServiceDto>>
    fun insertService(service: ServiceDto): Single<Long>
    suspend fun insertService(service: Service): Long
    fun updateService(vararg services: ServiceDto): Completable
    suspend fun updateServiceSuspend(vararg services: Service)
    fun deleteService(vararg services: ServiceDto): Completable
    fun deleteService(id: Long): Completable
    suspend fun delete(id: Long)
}