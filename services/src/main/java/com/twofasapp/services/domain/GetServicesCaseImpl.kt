package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service
import com.twofasapp.services.data.ServicesRepository
import kotlinx.coroutines.flow.first

internal class GetServicesCaseImpl(
    private val servicesRepository: ServicesRepository,
) : GetServicesCase {

    override suspend fun invoke(): List<Service> {
        return servicesRepository.selectFlow().first().filter { it.isDeleted.not() }
    }
}