package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.ServiceParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader

internal class AndOtpImporter(
    private val context: Context,
    private val jsonSerializer: Json,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {


    @Serializable
    data class Model(
        val secret: String,
        val issuer: String?,
        val label: String?,
        val digits: Int?,
        val period: Int?,
        val counter: Int?,
        val type: String?,
        val algorithm: String?,
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
            val models = jsonSerializer.decodeFromString<List<Model>>(json)

            fileDescriptor?.close()
            inputStream.close()

            val totalServices = models.size
            val servicesToImport = mutableListOf<Service?>()

            models
                .filter { it.digits == 5 || it.digits == 6 || it.digits == 7 || it.digits == 8 || it.digits == null }
                .filter { it.period == 10 || it.period == 30 || it.period == 60 || it.period == 90 || it.period == null }
                .filter {
                    it.algorithm.equals("SHA1", true) ||
                            it.algorithm.equals("SHA224", true) ||
                            it.algorithm.equals("SHA256", true) ||
                            it.algorithm.equals("SHA384", true) ||
                            it.algorithm.equals("SHA512", true) ||
                            it.algorithm == null
                }
                .filter { servicesRepository.isSecretValid(it.secret) }
                .forEach { entry ->
                    servicesToImport.add(parseService(entry))
                }

            return ExternalImport.Success(
                servicesToImport = servicesToImport.filterNotNull(),
                totalServicesCount = totalServices,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }

    private fun parseService(model: Model): Service? {
        val otpLink = OtpAuthLink(
            type = model.type ?: "TOTP",
            label = model.label ?: "",
            secret = model.secret,
            issuer = model.issuer,
            params = parseParams(model),
            link = null,
        )

        return ServiceParser.parseService(otpLink)
    }

    private fun parseParams(model: Model): Map<String, String> {
        val params = mutableMapOf<String, String>()
        model.algorithm?.let { params[OtpAuthLink.ParamAlgorithm] = it }
        model.counter?.let { params[OtpAuthLink.ParamPeriod] = it.toString() }
        model.digits?.let { params[OtpAuthLink.ParamDigits] = it.toString() }
        model.counter?.let { params[OtpAuthLink.ParamCounter] = it.toString() }
        return params
    }
}