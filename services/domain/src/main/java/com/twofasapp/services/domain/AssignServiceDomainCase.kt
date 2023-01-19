package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service

interface AssignServiceDomainCase {

    suspend operator fun invoke(service: Service, domain: String)
}