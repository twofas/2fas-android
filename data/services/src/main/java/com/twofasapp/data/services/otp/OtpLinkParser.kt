package com.twofasapp.data.services.otp

import android.net.Uri
import com.twofasapp.parsers.domain.OtpAuthLink
import timber.log.Timber

// TODO: This object should replace ParseOtpAuthLink
object OtpLinkParser {

    private const val OTPAUTH = "otpauth"
    private const val TOTP = "totp"
    private const val HOTP = "hotp"
    private const val SECRET = "secret"
    private const val ISSUER = "issuer"

    fun parse(link: String): OtpAuthLink? {
        try {
            Timber.d("Parse link: $link")

            val decoded = Uri.decode(link).replace("#", "-") // remove hash, Uri.parse terminates on that
            val uri = Uri.parse(decoded)

            if (isUriValid(uri).not()) {
                return null
//                throw IllegalArgumentException("Link is not supported")
            }

            if (isAuthorityValid(uri).not()) {
                return null
//                throw IllegalArgumentException("Only TOTP and HOTP are supported")
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

            return otpAuthLink


        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    private fun isUriValid(uri: Uri?) = uri?.scheme?.lowercase() == OTPAUTH

    private fun isAuthorityValid(uri: Uri) =
        uri.authority?.lowercase() == TOTP || uri.authority?.lowercase() == HOTP

    private fun mapQueryParams(uri: Uri) = uri.queryParameterNames.map { it to uri.getQueryParameter(it)!! }.toMap()

    private fun getPath(uri: Uri): String {
        if (uri.path == null) return ""

        return if (uri.path!!.startsWith("/")) uri.path!!.drop(1) else uri.path!!
    }

    data class Params(
        val link: String
    )
}