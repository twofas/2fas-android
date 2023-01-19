package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service

interface GetServicesCase {
    suspend operator fun invoke(): List<Service>
}