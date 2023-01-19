package com.twofasapp.services.domain

import com.twofasapp.services.data.ServicesRepository
import com.twofasapp.services.domain.model.Service

internal class AssignServiceDomainCaseImpl(
    private val servicesRepository: ServicesRepository,
) : AssignServiceDomainCase {

    override suspend fun invoke(service: Service, domain: String) {
        servicesRepository.assignServiceDomain(service, domain)
    }
}