package com.twofasapp.services.domain

import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.services.domain.model.Service

interface ConvertOtpToServiceCase {
    operator fun invoke(otp: OtpAuthLink): Service
}