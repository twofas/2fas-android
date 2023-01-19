package com.twofasapp.services.domain

import com.twofasapp.parsers.ServiceParser
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.prefs.model.OtpAuthLink
import com.twofasapp.services.data.converter.toService
import com.twofasapp.services.domain.model.Service

class ConvertOtpToServiceCaseImpl : ConvertOtpToServiceCase {

    override fun invoke(otp: OtpAuthLink): Service {
        val supportedService = SupportedServices.list.firstOrNull { it.isMatching(otp.issuer, otp.label) }
        return ServiceParser.parseService(otp, supportedService).toService()
    }
}