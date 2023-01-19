package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.services.domain.DeleteServiceUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import java.util.*

class DeleteDuplicatedService(
    private val getServicesIncludingTrashed: GetServicesIncludingTrashed,
    private val deleteServiceUseCase: DeleteServiceUseCase
) : UseCaseParameterized<String, Completable> {

    override fun execute(params: String, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Completable {
        return getServicesIncludingTrashed.execute(subscribeScheduler, subscribeScheduler)
            .flatMapCompletable {
                val duplicatedService = it.firstOrNull { it.secret.lowercase(Locale.US) == params.lowercase(Locale.US) }

                if (duplicatedService == null) {
                    Completable.complete()
                } else {
                    deleteServiceUseCase.execute(duplicatedService, subscribeScheduler, subscribeScheduler)
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    }
}