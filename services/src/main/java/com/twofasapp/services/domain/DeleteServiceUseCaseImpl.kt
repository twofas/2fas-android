package com.twofasapp.services.domain

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.widgets.domain.WidgetActions
import io.reactivex.Completable
import io.reactivex.Scheduler

internal class DeleteServiceUseCaseImpl(
    private val servicesRepository: ServicesRepository,
    private val widgetActions: WidgetActions,
    private val storeServicesOrder: StoreServicesOrder,
) : DeleteServiceUseCase {

    override fun execute(params: ServiceDto, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        return servicesRepository.deleteService(params)
            .doOnComplete {
                storeServicesOrder.deleteFromOrder(params.id)
            }
            .doOnComplete {
                widgetActions.onServiceDeleted(params.id)
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}