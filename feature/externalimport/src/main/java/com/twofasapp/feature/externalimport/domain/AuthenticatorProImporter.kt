package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.data.services.otp.ServiceParser
import java.io.BufferedReader

internal class AuthenticatorProImporter(
    private val context: Context,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {

    override fun isSchemaSupported(content: String): Boolean {
        return true
    }

    override fun read(content: String): ExternalImport {
        try {
            val fileUri = Uri.parse(content)
            val fileDescriptor = context.contentResolver.openAssetFileDescriptor(fileUri, "r")
            val size = fileDescriptor?.length ?: 0

            if (size > 10 * 1024 * 1024) {
                return ExternalImport.ParsingError(RuntimeException("File too big"))
            }

            val inputStream = context.contentResolver.openInputStream(fileUri)!!
            val text = inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)

            fileDescriptor?.close()
            inputStream.close()

            val totalServices = text.lines().filter { it.isNotBlank() }.size
            val servicesToImport = mutableListOf<Service?>()

            text.lines()
                .filter { it.isNotBlank() }
                .filter { it.startsWith("otpauth", ignoreCase = true) }
                .mapNotNull { link ->
                    // Detect steam link
                    if (link.contains("issuer=steam", ignoreCase = true) || link.contains("&steam", ignoreCase = true)) {
                        val otpLink = OtpLinkParser.parse(link)
                        otpLink?.copy(
                            type = "STEAM",
                            params = mapOf(
                                OtpAuthLink.ParamDigits to "5",
                                OtpAuthLink.ParamPeriod to "30",
                                OtpAuthLink.ParamAlgorithm to "SHA1",
                            )
                        )
                    } else {
                        OtpLinkParser.parse(link)
                    }
                }
                .filter { servicesRepository.isServiceValid(it) }
                .forEach { entry -> servicesToImport.add(ServiceParser.parseService(entry)) }

            return ExternalImport.Success(
                servicesToImport = servicesToImport.filterNotNull(),
                totalServicesCount = totalServices,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }
}