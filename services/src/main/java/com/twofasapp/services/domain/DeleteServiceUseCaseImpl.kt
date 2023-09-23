package com.twofasapp.services.domain

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.common.domain.WidgetCallbacks
import io.reactivex.Completable
import io.reactivex.Scheduler

internal class DeleteServiceUseCaseImpl(
    private val servicesRepository: ServicesRepository,
    private val widgetCallbacks: WidgetCallbacks,
    private val storeServicesOrder: StoreServicesOrder,
) : DeleteServiceUseCase {

    override fun execute(params: ServiceDto, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        return servicesRepository.deleteService(params)
            .doOnComplete {
                storeServicesOrder.deleteFromOrder(params.id)
            }
            .doOnComplete {
                widgetCallbacks.onServiceDeleted(params.id)
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}