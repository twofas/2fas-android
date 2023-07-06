package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.serialization.JsonSerializer
import com.twofasapp.services.domain.ConvertOtpLinkToService
import kotlinx.serialization.Serializable
import java.io.BufferedReader

internal class LastPassImporter(
    private val context: Context,
    private val jsonSerializer: JsonSerializer,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {

    @Serializable
    data class Model(
        val accounts: List<Account>,
    )

    @Serializable
    data class Account(
        val issuerName: String,
        val userName: String,
        val secret: String,
        val algorithm: String,
        val timeStep: Int,
        val digits: Int,
    )

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
            val json = inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
            val model = jsonSerializer.deserialize<Model>(json)

            fileDescriptor?.close()
            inputStream.close()

            val totalServices = model.accounts.size
            val servicesToImport = mutableListOf<ServiceDto>()

            model
                .accounts
                .filter { it.digits == 6 || it.digits == 7 || it.digits == 8 }
                .filter { it.timeStep == 30 || it.timeStep == 60 || it.timeStep == 90 }
                .filter {
                    it.algorithm.equals("SHA1", true) || it.algorithm.equals("SHA224", true) || it.algorithm.equals(
                        "SHA256",
                        true
                    ) || it.algorithm.equals("SHA384", true) || it.algorithm.equals("SHA512", true)
                }
                .filter { servicesRepository.isSecretValid(it.secret) }
                .forEach { entry ->
                    servicesToImport.add(parseService(entry))
                }

            return ExternalImport.Success(
                servicesToImport = servicesToImport,
                totalServicesCount = totalServices,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }

    private fun parseService(account: Account): ServiceDto {
        val otpLink = OtpAuthLink(
            type = "TOTP",
            label = account.userName,
            secret = account.secret,
            issuer = account.issuerName,
            params = parseParams(account),
            link = null,
        )

        val parsed = convertOtpLinkToService.execute(otpLink)

        return parsed.copy(otpAccount = account.userName)
    }

    private fun parseParams(account: Account): Map<String, String> {
        val params = mutableMapOf<String, String>()
        account.algorithm.let { params[OtpAuthLink.ALGORITHM_PARAM] = it }
        account.timeStep.let { params[OtpAuthLink.PERIOD_PARAM] = it.toString() }
        account.digits.let { params[OtpAuthLink.DIGITS_PARAM] = it.toString() }
        return params
    }
}