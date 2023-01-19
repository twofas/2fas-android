package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ObserveServiceCase {
    operator fun invoke(serviceId: Long): Flow<Service>
}