package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.serialization.JsonSerializer
import com.twofasapp.services.domain.ConvertOtpLinkToService
import kotlinx.serialization.Serializable
import java.io.BufferedReader

internal class AegisImporter(
    private val context: Context,
    private val jsonSerializer: JsonSerializer,
    private val convertOtpLinkToService: ConvertOtpLinkToService,
    private val analyticsService: AnalyticsService,
) : ExternalImporter {

    @Serializable
    data class Model(
        val db: Db,
    )

    @Serializable
    data class Db(
        val entries: List<Entry>,
    )

    @Serializable
    data class Entry(
        val type: String,
        val name: String,
        val issuer: String,
        val note: String,
        val info: Info,
    ) {
        @Serializable
        data class Info(
            val secret: String,
            val algo: String?,
            val digits: Int?,
            val period: Int?,
            val counter: Int?,
        )
    }

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

            val totalServices = model.db.entries.size
            val servicesToImport = mutableListOf<ServiceDto>()

            model.db.entries
                .filter { it.type.equals("totp", true) || it.type.equals("hotp", true) }
                .filter { it.info.digits == 6 || it.info.digits == 7 || it.info.digits == 8 }
                .filter { it.info.period == 30 || it.info.period == 60 || it.info.period == 90 }
                .filter {
                    it.info.algo.equals("SHA1", true) || it.info.algo.equals("SHA224", true) || it.info.algo.equals(
                        "SHA256",
                        true
                    ) || it.info.algo.equals("SHA384", true) || it.info.algo.equals("SHA512", true)
                }
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

    private fun parseService(entry: Entry): ServiceDto {
        val otpLink = com.twofasapp.parsers.domain.OtpAuthLink(
            type = entry.type.uppercase(),
            label = entry.name,
            secret = entry.info.secret,
            issuer = entry.issuer,
            params = parseParams(entry),
            link = null,
        )

        val parsed = convertOtpLinkToService.execute(otpLink)

        return parsed.copy(name = entry.name, otpAccount = entry.note)
    }

    private fun parseParams(entry: Entry): Map<String, String> {
        val params = mutableMapOf<String, String>()

        entry.info.algo?.let { params[com.twofasapp.parsers.domain.OtpAuthLink.ALGORITHM_PARAM] = it }
        entry.info.period?.let { params[com.twofasapp.parsers.domain.OtpAuthLink.PERIOD_PARAM] = it.toString() }
        entry.info.digits?.let { params[com.twofasapp.parsers.domain.OtpAuthLink.DIGITS_PARAM] = it.toString() }
        entry.info.counter?.let { params[com.twofasapp.parsers.domain.OtpAuthLink.COUNTER] = it.toString() }

        return params
    }
}