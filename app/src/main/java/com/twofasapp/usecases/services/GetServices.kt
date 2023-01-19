package com.twofasapp.usecases.services

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.GetServicesUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class GetServices(private val servicesRepository: ServicesRepository) : GetServicesUseCase {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<List<ServiceDto>> {
        return servicesRepository.select()
            .map { list -> list.filter { it.isDeleted != true } }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}