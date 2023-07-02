package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.domain.ConvertOtpLinkToService
import java.io.BufferedReader

internal class AuthenticatorProImporter(
    private val context: Context,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
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
            val servicesToImport = mutableListOf<ServiceDto>()

            text.lines()
                .filter { it.isNotBlank() }
                .mapNotNull { OtpLinkParser.parseLegacy(it) }
                .filter { servicesRepository.isServiceValid(it) }
                .forEach { entry -> servicesToImport.add(convertOtpLinkToService.execute(entry)) }

            return ExternalImport.Success(
                servicesToImport = servicesToImport,
                totalServicesCount = totalServices,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }
}