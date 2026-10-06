package com.twofasapp.feature.externalimport.domain

import android.content.Context
import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.otp.OtpLinkParser
import com.twofasapp.data.services.otp.ServiceParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader

/**
 * Supports unencrypted JSON and CSV exports from both Bitwarden Password Manager and Bitwarden Authenticator.
 */
internal class BitwardenImporter(
    private val context: Context,
    private val jsonSerializer: Json,
    private val servicesRepository: ServicesRepository,
) : ExternalImporter {

    @Serializable
    data class Model(
        val encrypted: Boolean = false,
        val passwordProtected: Boolean = false,
        val items: List<Item> = emptyList(),
    )

    @Serializable
    data class Item(
        val name: String? = null,
        val login: Login? = null,
    )

    @Serializable
    data class Login(
        val username: String? = null,
        val totp: String? = null,
    )

    private data class Entry(
        val name: String?,
        val username: String?,
        val totp: String,
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

            val entries = if (text.trimStart().startsWith("{")) {
                val model = jsonSerializer.decodeFromString<Model>(text)

                if (model.encrypted || model.passwordProtected) {
                    return ExternalImport.UnsupportedError(
                        "Encrypted Bitwarden exports are not supported. Export your data to an unencrypted file.",
                    )
                }

                model.items.mapNotNull { item ->
                    item.login?.totp?.takeIf { it.isNotBlank() }?.let { totp ->
                        Entry(name = item.name, username = item.login.username, totp = totp.trim())
                    }
                }
            } else {
                readCsv(text) ?: return ExternalImport.UnsupportedError("Unsupported Bitwarden CSV file. Missing \"login_totp\" column.")
            }

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
        val header = rows.firstOrNull()?.map { it.trim() } ?: return null

        val totpIndex = header.indexOf("login_totp").takeIf { it >= 0 } ?: return null
        val nameIndex = header.indexOf("name")
        val usernameIndex = header.indexOf("login_username")

        return rows.drop(1).mapNotNull { row ->
            row.getOrNull(totpIndex)?.trim()?.takeIf { it.isNotBlank() }?.let { totp ->
                Entry(
                    name = row.getOrNull(nameIndex),
                    username = row.getOrNull(usernameIndex),
                    totp = totp,
                )
            }
        }
    }

    private fun parseLink(entry: Entry): OtpAuthLink? {
        val link = when {
            entry.totp.startsWith("otpauth://", ignoreCase = true) -> OtpLinkParser.parse(entry.totp)

            entry.totp.startsWith("steam://", ignoreCase = true) -> OtpAuthLink(
                type = "STEAM",
                label = "",
                secret = entry.totp.substringAfter("://"),
                issuer = null,
                params = mapOf(
                    OtpAuthLink.ParamDigits to "5",
                    OtpAuthLink.ParamPeriod to "30",
                    OtpAuthLink.ParamAlgorithm to "SHA1",
                ),
                link = null,
            )

            // Plain secret
            else -> OtpAuthLink(
                type = "TOTP",
                label = "",
                secret = entry.totp,
                issuer = null,
                link = null,
            )
        } ?: return null

        return link.copy(
            secret = link.secret.replace(" ", "").uppercase(),
            issuer = link.issuer?.takeIf { it.isNotBlank() } ?: entry.name?.takeIf { it.isNotBlank() },
            label = link.label.ifBlank { entry.username.orEmpty() },
        )
    }

    private fun parseCsv(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < text.length) {
            val c = text[i]

            if (inQuotes) {
                when {
                    c == '"' && text.getOrNull(i + 1) == '"' -> {
                        field.append('"')
                        i++
                    }

                    c == '"' -> inQuotes = false
                    else -> field.append(c)
                }
            } else {
                when (c) {
                    '"' -> inQuotes = true
                    ',' -> {
                        row.add(field.toString())
                        field.clear()
                    }

                    '\r' -> Unit
                    '\n' -> {
                        row.add(field.toString())
                        field.clear()
                        rows.add(row)
                        row = mutableListOf()
                    }

                    else -> field.append(c)
                }
            }

            i++
        }

        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }

        return rows
    }
}