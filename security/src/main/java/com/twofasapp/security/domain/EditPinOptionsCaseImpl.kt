package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.PinOptions

internal class EditPinOptionsCaseImpl(
    private val securityRepository: SecurityRepository,
) : EditPinOptionsCase {

    override suspend fun invoke(pinOptions: PinOptions) {
        securityRepository.editPinOptions(pinOptions)
    }
}