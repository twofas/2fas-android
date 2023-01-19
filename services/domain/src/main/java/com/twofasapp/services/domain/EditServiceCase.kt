package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service

interface EditServiceCase {
    suspend operator fun invoke(service: Service)
}