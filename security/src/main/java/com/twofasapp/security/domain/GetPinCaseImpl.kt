package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository

internal class GetPinCaseImpl(
    private val securityRepository: SecurityRepository,
) : GetPinCase {

    override suspend fun invoke(): String {
        return securityRepository.getPin()
    }
}