package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.PinOptions
import kotlinx.coroutines.flow.Flow

internal class ObservePinOptionsCaseImpl(
    private val securityRepository: SecurityRepository,
) : ObservePinOptionsCase {

    override fun invoke(): Flow<PinOptions> {
        return securityRepository.observePinOptions()
    }
}