package com.twofasapp.services.domain

import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ObserveServicesCase {
    operator fun invoke(): Flow<List<Service>>
}