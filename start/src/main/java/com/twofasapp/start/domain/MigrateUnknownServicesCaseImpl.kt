package com.twofasapp.start.domain

import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.services.domain.ConvertOtpToServiceCase
import com.twofasapp.services.domain.EditServiceCase
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.model.Service

class MigrateUnknownServicesCaseImpl(
    private val getServicesCase: GetServicesCase,
    private val editServiceCase: EditServiceCase,
    private val convertOtpToServiceCase: ConvertOtpToServiceCase,
) : MigrateUnknownServicesCase {

    override suspend fun invoke() {
        val servicesToMigrate = getServicesCase().filter { service ->
            service.serviceTypeId.isNullOrEmpty()
                    && service.iconCollectionId == ServiceIcons.defaultCollectionId
                    && service.selectedImageType == Service.ImageType.IconCollection
        }

        val servicesMigrated = servicesToMigrate.map { migrateService(it) }

        servicesMigrated.forEach { editServiceCase(it) }
    }

    private fun migrateService(service: Service): Service {
        val otpLink = OtpAuthLink(
            type = service.authType.name,
            label = service.otp.label,
            issuer = service.otp.issuer,
            secret = service.secret,
            params = emptyMap(),
            link = service.otp.link,
        )

        val matchedService = convertOtpToServiceCase(otpLink)

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