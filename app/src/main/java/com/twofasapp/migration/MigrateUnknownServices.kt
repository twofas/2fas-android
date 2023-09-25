package com.twofasapp.migration

import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.ServiceParser
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.domain.OtpAuthLink

class MigrateUnknownServices(
    private val servicesRepository: ServicesRepository,
) {

    suspend fun invoke() {
        val servicesToMigrate = servicesRepository.getServices().filter { service ->
            service.serviceTypeId.isNullOrEmpty()
                    && service.iconCollectionId == ServiceIcons.defaultCollectionId
                    && service.imageType == Service.ImageType.IconCollection
        }

        val servicesMigrated = servicesToMigrate.map { migrateService(it) }

        servicesMigrated.forEach {
            servicesRepository.updateService(it)
        }
    }

    private fun migrateService(service: Service): Service {
        val otpLink = OtpAuthLink(
            type = service.authType.name,
            label = service.info.orEmpty(),
            issuer = service.issuer,
            secret = service.secret,
            params = emptyMap(),
            link = service.link,
        )

        val matchedService = ServiceParser.parseService(otpLink)

        return if (matchedService.serviceTypeId == null) {
            service
        } else {
            service.copy(
                serviceTypeId = matchedService.serviceTypeId,
                iconCollectionId = matchedService.iconCollectionId,
            )
        }
    }
}