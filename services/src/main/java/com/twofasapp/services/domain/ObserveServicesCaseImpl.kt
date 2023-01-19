package com.twofasapp.services.domain

import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.Flow

internal class ObserveServicesCaseImpl(
    private val servicesRepository: ServicesRepository,
) : ObserveServicesCase {

    override operator fun invoke(): Flow<List<Service>> {
        return servicesRepository.selectFlow()
    }
}