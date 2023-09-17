package com.twofasapp.usecases.services

import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.common.ktx.removeWhiteCharacters
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.Locale

class CheckServiceExists(private val getServices: GetServices) : UseCaseParameterized<String, Single<Boolean>> {

    override fun execute(params: String, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<Boolean> {
        return getServices.execute(subscribeScheduler, subscribeScheduler)
            .map {
                it.map { service -> service.secret.removeWhiteCharacters().lowercase(Locale.US) }
                    .contains(params.removeWhiteCharacters().lowercase(Locale.US))
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }
}