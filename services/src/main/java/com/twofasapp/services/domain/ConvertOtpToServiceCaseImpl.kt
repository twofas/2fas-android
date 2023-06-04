package com.twofasapp.services.domain

import com.twofasapp.parsers.LegacyServiceParser
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.services.data.converter.toService
import com.twofasapp.services.domain.model.Service

class ConvertOtpToServiceCaseImpl : ConvertOtpToServiceCase {

    override fun invoke(otp: OtpAuthLink): Service {
        val supportedService = SupportedServices.list.firstOrNull { it.isMatching(otp.issuer, otp.label) }
        return LegacyServiceParser.parseServiceDto(otp, supportedService).toService()
    }
}