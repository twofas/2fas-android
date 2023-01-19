package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.PinOptions

interface EditPinOptionsCase {
    suspend operator fun invoke(pinOptions: PinOptions)
}