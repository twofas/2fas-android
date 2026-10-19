package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.data.services.otp.ServiceParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader

/**
 * Supports plain text exports from Ente Auth - one otpauth:// link per line, with Ente metadata in "codeDisplay" param.
 */
internal class EnteImporter(
    private val context: Context,
    private val jsonSerializer: Json,
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
            val text = inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText).removePrefix("")

            fileDescriptor?.close()
            inputStream.close()

            val links = text.lines()
                .map { it.trim() }
                .filter { it.startsWith("otpauth://", ignoreCase = true) }
                .filterNot { isTrashed(it) }
                .map { removeCodeDisplay(it) }

            val servicesToImport = links
                .mapNotNull { OtpLinkParser.parse(it) }
                .filter { servicesRepository.isServiceValid(it) }
                .map { ServiceParser.parseService(it) }

            return ExternalImport.Success(
                servicesToImport = servicesToImport,
                totalServicesCount = links.size,
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ExternalImport.ParsingError(e)
        }
    }

    private fun isTrashed(link: String): Boolean {
        val codeDisplay = link.substringAfter('?', "")
            .split('&')
            .firstOrNull { it.startsWith("$CodeDisplayParam=") }
            ?.substringAfter('=')
            ?: return false

        return try {
            jsonSerializer.parseToJsonElement(Uri.decode(codeDisplay))
                .jsonObject["trashed"]
                ?.jsonPrimitive
                ?.booleanOrNull == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * OtpLinkParser decodes the whole link before parsing, so special characters inside
     * "codeDisplay" JSON (e.g. in notes) could break other params.
     */
    private fun removeCodeDisplay(link: String): String {
        if (link.contains('?').not()) return link

        val params = link.substringAfter('?')
            .split('&')
            .filterNot { it.startsWith("$CodeDisplayParam=") }
            .joinToString("&")

        return "${link.substringBefore('?')}?$params"
    }

    private companion object {
        const val CodeDisplayParam = "codeDisplay"
    }
}