package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.prefs.model.ServiceDto
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.Locale

class GetService(
    private val getServices: GetServices
) : UseCaseParameterized<String, Single<ServiceDto>> {

    override fun execute(params: String, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<ServiceDto> {
        return getServices.execute(subscribeScheduler, observeScheduler)
            .map { list -> list.first { it.secret.lowercase(Locale.US) == params.lowercase(Locale.US) } }
    }

}