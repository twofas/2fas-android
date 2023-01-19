package com.twofasapp.usecases.services

import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.data.ServicesRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UpdateServiceGroup(
    private val servicesRepository: ServicesRepository,
) {

    fun execute(services: List<ServiceDto>, groupId: String?): Completable {
        return servicesRepository.updateService(
            *services.map { it.copy(groupId = groupId) }.toTypedArray()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
