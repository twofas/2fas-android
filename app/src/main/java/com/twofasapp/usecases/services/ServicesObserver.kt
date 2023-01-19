package com.twofasapp.usecases.services

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler

class ServicesObserver(
    private val servicesRepository: ServicesRepository
) : UseCase<Flowable<List<ServiceDto>>> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Flowable<List<ServiceDto>> {
        return servicesRepository.observe()
            .map { list -> list.filter { it.isDeleted != true } }
            .share()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}