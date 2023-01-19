package com.twofasapp.services.domain

import com.twofasapp.prefs.model.OtpAuthLink
import com.twofasapp.services.domain.model.Service

interface ConvertOtpToServiceCase {
    operator fun invoke(otp: OtpAuthLink): Service
}