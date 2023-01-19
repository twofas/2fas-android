package com.twofasapp.services.domain

import com.twofasapp.parsers.ServiceParser
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.prefs.model.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto

class ConvertOtpLinkToService {

    fun execute(link: OtpAuthLink): ServiceDto {
        val supportedService = SupportedServices.list.firstOrNull { it.isMatching(link.issuer, link.label) }
        return ServiceParser.parseService(link, supportedService)
    }
}