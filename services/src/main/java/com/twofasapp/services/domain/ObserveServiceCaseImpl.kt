package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service
import com.twofasapp.services.data.ServicesRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveServiceCaseImpl(
    private val servicesRepository: ServicesRepository,
) : ObserveServiceCase {

    override fun invoke(serviceId: Long): Flow<Service> {
        return servicesRepository.observe(serviceId)
    }
}