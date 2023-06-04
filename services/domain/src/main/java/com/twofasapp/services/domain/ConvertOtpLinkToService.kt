package com.twofasapp.services.domain

import com.twofasapp.parsers.LegacyServiceParser
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto

class ConvertOtpLinkToService {

    fun execute(link: OtpAuthLink): ServiceDto {
        val supportedService = SupportedServices.list.firstOrNull { it.isMatching(link.issuer, link.label) }
        return LegacyServiceParser.parseServiceDto(link, supportedService)
    }
}