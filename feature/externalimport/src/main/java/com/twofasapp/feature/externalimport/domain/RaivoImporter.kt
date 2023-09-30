package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.otp.ServiceParser
import com.twofasapp.common.domain.OtpAuthLink
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader

internal class RaivoImporter(
    private val context: Context,
    private val jsonSerializer: Json,
) : ExternalImporter {

    @Serializable
    data class Entry(
        val kind: String,
        val issuer: String,
        val account: String,
        val secret: String,
        val timer: String?,
        val digits: String?,
        val algorithm: String?,
        val counter: String?,
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
            val model = jsonSerializer.decodeFromString<List<Entry>>(json)

            fileDescriptor?.close()
            inputStream.close()

            val totalServices = model.size
            val servicesToImport = mutableListOf<Service?>()

            model
                .filter { it.kind.equals("totp", true) || it.kind.equals("hotp", true) }
                .filter { it.digits.equals("6") || it.digits.equals("7") || it.digits.equals("8") }
                .filter { it.timer.equals("30") || it.timer.equals("60") || it.timer.equals("90") }
                .filter {
                    it.algorithm.equals("SHA1", true) || it.algorithm.equals("SHA224", true) || it.algorithm.equals(
                        "SHA256",
                        true
                    ) || it.algorithm.equals("SHA384", true) || it.algorithm.equals("SHA512", true)
                }
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

    private fun parseService(entry: Entry): Service {
        val otpLink = OtpAuthLink(
            type = entry.kind.uppercase(),
            label = entry.account,
            secret = entry.secret,
            issuer = entry.issuer,
            params = parseParams(entry),
            link = null,
        )

        val parsed = ServiceParser.parseService(otpLink)

        return parsed.copy(info = entry.account)
    }

    private fun parseParams(entry: Entry): Map<String, String> {
        val params = mutableMapOf<String, String>()

        entry.algorithm?.let { params[OtpAuthLink.ParamAlgorithm] = it }
        entry.timer?.let { params[OtpAuthLink.ParamPeriod] = it }
        entry.digits?.let { params[OtpAuthLink.ParamDigits] = it }
        entry.counter?.let { params[OtpAuthLink.ParamCounter] = it }

        return params
    }
}