package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.InvalidPinStatus
import kotlinx.coroutines.flow.Flow

internal class ObserveInvalidPinStatusCaseImpl(
    private val securityRepository: SecurityRepository,
) : ObserveInvalidPinStatusCase {

    override fun invoke(): Flow<InvalidPinStatus> {
        return securityRepository.observeInvalidPinStatus()
    }
}