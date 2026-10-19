package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.data.services.otp.ServiceParser
import java.io.BufferedReader

/**
 * Supports CSV exports from 1Password (Title,Url,Username,Password,OTPAuth,Favorite,Archived,Tags,Notes).
 */
internal class OnePasswordImporter(
    private val context: Context,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {

    private data class Entry(
        val title: String?,
        val username: String?,
        val otp: String,
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
            val text = inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText).removePrefix("")

            fileDescriptor?.close()
            inputStream.close()

            val entries = readCsv(text) ?: return ExternalImport.UnsupportedError("Unsupported 1Password CSV file. Missing \"OTPAuth\" column.")

            val servicesToImport = entries
                .mapNotNull { parseLink(it) }
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

    private fun readCsv(text: String): List<Entry>? {
        val rows = parseCsv(text).filter { row -> row.any { it.isNotBlank() } }
        val header = rows.firstOrNull()?.map { it.trim().lowercase() } ?: return null

        val otpIndex = header.indexOf("otpauth").takeIf { it >= 0 } ?: return null
        val titleIndex = header.indexOf("title")
        val usernameIndex = header.indexOf("username")

        return rows.drop(1).mapNotNull { row ->
            row.getOrNull(otpIndex)?.trim()?.takeIf { it.isNotBlank() }?.let { otp ->
                Entry(
                    title = row.getOrNull(titleIndex),
                    username = row.getOrNull(usernameIndex),
                    otp = otp,
                )
            }
        }
    }

    private fun parseLink(entry: Entry): OtpAuthLink? {
        val link = if (entry.otp.startsWith("otpauth://", ignoreCase = true)) {
            OtpLinkParser.parse(entry.otp)
        } else {
            // Plain secret
            OtpAuthLink(
                type = "TOTP",
                label = "",
                secret = entry.otp,
                issuer = null,
                link = null,
            )
        } ?: return null

        return link.copy(
            secret = link.secret.replace(" ", "").uppercase(),
            issuer = link.issuer?.takeIf { it.isNotBlank() } ?: entry.title?.takeIf { it.isNotBlank() },
            label = link.label.ifBlank { entry.username.orEmpty() },
        )
    }
}