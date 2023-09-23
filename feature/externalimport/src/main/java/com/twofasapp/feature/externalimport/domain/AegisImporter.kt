package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.ServiceParser
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.serialization.JsonSerializer
import kotlinx.serialization.Serializable
import java.io.BufferedReader

internal class AegisImporter(
    private val context: Context,
    private val jsonSerializer: JsonSerializer,
    private val servicesRepository: ServicesRepository,
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
        val name: String, // info
        val issuer: String, // issuer and name
        val note: String, // not relevant
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
            val servicesToImport = mutableListOf<Service?>()

            model.db.entries
                .asSequence()
                .filter { it.type.equals("totp", true) || it.type.equals("hotp", true) }
                .filter { it.info.digits == 6 || it.info.digits == 7 || it.info.digits == 8 }
                .filter { it.info.period == 30 || it.info.period == 60 || it.info.period == 90 }
                .filter {
                    it.info.algo.equals("SHA1", true) || it.info.algo.equals("SHA224", true) || it.info.algo.equals(
                        "SHA256",
                        true
                    ) || it.info.algo.equals("SHA384", true) || it.info.algo.equals("SHA512", true)
                }
                .filter { servicesRepository.isSecretValid(it.info.secret) }
                .toList()
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

    private fun parseService(entry: Entry): Service? {
        return ServiceParser.parseService(
            OtpAuthLink(
                type = entry.type.uppercase(),
                label = if (entry.issuer.isNotBlank()) "${entry.name}:${entry.name}" else entry.name,
                secret = entry.info.secret,
                issuer = entry.issuer,
                link = null,
                params = parseParams(entry),
            )
        )
    }

    private fun parseParams(entry: Entry): Map<String, String> {
        val params = mutableMapOf<String, String>()

        entry.info.algo?.let { params[OtpAuthLink.ALGORITHM_PARAM] = it }
        entry.info.period?.let { params[OtpAuthLink.PERIOD_PARAM] = it.toString() }
        entry.info.digits?.let { params[OtpAuthLink.DIGITS_PARAM] = it.toString() }
        entry.info.counter?.let { params[OtpAuthLink.COUNTER] = it.toString() }

        return params
    }
}