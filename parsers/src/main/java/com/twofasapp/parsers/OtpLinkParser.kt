package com.twofasapp.parsers

import android.net.Uri
import com.twofasapp.parsers.domain.OtpAuthLink
import timber.log.Timber
import java.util.Locale

object OtpLinkParser {

    private const val OTPAUTH = "otpauth"
    private const val TOTP = "totp"
    private const val HOTP = "hotp"
    private const val SECRET = "secret"
    private const val ISSUER = "issuer"

    fun parseOtpAuthLink(link: String): OtpAuthLink {
        return try {

            if (BuildConfig.DEBUG) {
                Timber.i("Parse link: $link")
            }

            val decoded = Uri.decode(link)
                .replace("#", "-") // remove hash, Uri.parse terminates on that
            val uri = Uri.parse(decoded)

            if (isUriValid(uri).not()) {
                throw IllegalArgumentException("Link is not supported")
            }

            if (isAuthorityValid(uri).not()) {
                throw IllegalArgumentException("Only TOTP and HOTP are supported")
            }

            val type = uri.authority!!
            val label = getPath(uri)
            val secret = uri.getQueryParameter(SECRET) ?: ""
            val issuer = uri.getQueryParameter(ISSUER)
            val queryParams = mapQueryParams(uri)

            val otpAuthLink = OtpAuthLink(
                type = type,
                label = label,
                secret = secret,
                issuer = issuer,
                params = queryParams,
                link = link,
            )

            otpAuthLink
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("InvalidOtpLinkFormat: ${e.message}")
        }
    }

    private fun isUriValid(uri: Uri?) = uri?.scheme?.lowercase(Locale.getDefault()) == OTPAUTH

    private fun isAuthorityValid(uri: Uri) =
        uri.authority?.lowercase(Locale.getDefault()) == TOTP || uri.authority?.lowercase(Locale.getDefault()) == HOTP

    private fun mapQueryParams(uri: Uri) = uri.queryParameterNames.associateWith { uri.getQueryParameter(it)!! }

    private fun getPath(uri: Uri): String {
        if (uri.path == null) return ""

        return if (uri.path!!.startsWith("/")) uri.path!!.drop(1) else uri.path!!
    }
}