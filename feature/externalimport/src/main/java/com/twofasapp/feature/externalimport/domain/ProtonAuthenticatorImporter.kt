package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.data.services.otp.ServiceParser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader

internal class ProtonAuthenticatorImporter(
    private val context: Context,
    private val jsonSerializer: Json,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {

    @Serializable
    data class Model(
        val entries: List<Entry>? = null,
    )

    @Serializable
    data class Entry(
        val content: Content,
    )

    @Serializable
    data class Content(
        val uri: String,
        @SerialName("entry_type")
        val entryType: String? = null,
        val name: String? = null,
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
            val model = jsonSerializer.decodeFromString<Model>(json)

            fileDescriptor?.close()
            inputStream.close()

            // Password protected exports don't contain plain "entries"
            val entries = model.entries ?: return ExternalImport.UnsupportedError(
                "Encrypted Proton Authenticator exports are not supported. Export your data to an unencrypted file.",
            )

            val servicesToImport = entries
                .mapNotNull { parseLink(it.content) }
                .filter { servicesRepository.isServiceValid(it) }
                .map { ServiceParser.parseService(it) }

            return ExternalImport.Success(
                servicesToImport = servicesToImport,
                totalServicesCount = entries.size,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }

    private fun parseLink(content: Content): OtpAuthLink? {
        val uri = content.uri.trim()

        val link = when {
            uri.startsWith("otpauth://", ignoreCase = true) -> OtpLinkParser.parse(uri)

            uri.startsWith("steam://", ignoreCase = true) || content.entryType.equals("Steam", ignoreCase = true) -> OtpAuthLink(
                type = "STEAM",
                label = "",
                secret = uri.substringAfter("://"),
                issuer = "Steam",
                params = mapOf(
                    OtpAuthLink.ParamDigits to "5",
                    OtpAuthLink.ParamPeriod to "30",
                    OtpAuthLink.ParamAlgorithm to "SHA1",
                ),
                link = null,
            )

            else -> null
        } ?: return null

        return link.copy(
            issuer = link.issuer?.takeIf { it.isNotBlank() } ?: content.name?.takeIf { it.isNotBlank() },
            label = link.label.ifBlank { content.name.orEmpty() },
        )
    }
}