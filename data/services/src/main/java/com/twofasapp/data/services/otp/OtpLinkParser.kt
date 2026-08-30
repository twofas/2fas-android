package com.twofasapp.data.services.otp

import android.net.Uri
import com.twofasapp.common.domain.OtpAuthLink
import timber.log.Timber

object OtpLinkParser {

    private const val OTPAUTH = "otpauth"
    private const val TOTP = "totp"
    private const val HOTP = "hotp"
    private const val STEAM = "steam"
    private const val SECRET = "secret"
    private const val ISSUER = "issuer"

    fun parse(link: String): OtpAuthLink? {
        try {
            Timber.d("Parse link: $link")

            val decoded = Uri.decode(link).replace("#", "-") // remove hash, Uri.parse terminates on that
            val uri = Uri.parse(decoded)

            if (isUriValid(uri).not()) {
                return null
            }

            if (isAuthorityValid(uri).not()) {
                return null
            }

            val type = uri.authority!!
            val label = getPath(uri)
            val secret = uri.getQueryParameter(SECRET) ?: ""
            val issuer = uri.getQueryParameter(ISSUER)
            val queryParams = mapQueryParams(uri)

            return OtpAuthLink(
                type = type,
                label = label,
                secret = secret,
                issuer = issuer,
                params = queryParams,
                link = link,
            )


        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    private fun isUriValid(uri: Uri?) = uri?.scheme?.lowercase() == OTPAUTH

    private fun isAuthorityValid(uri: Uri) =
        uri.authority?.lowercase() == TOTP || uri.authority?.lowercase() == HOTP || uri.authority?.lowercase() == STEAM

    private fun mapQueryParams(uri: Uri) = uri.queryParameterNames.associateWith {
        uri.getQueryParameter(it) ?: "" // getQueryParameter() is nullable when parameters key contains semicolon
    }

    private fun getPath(uri: Uri): String {
        if (uri.path == null) return ""

        return if (uri.path!!.startsWith("/")) uri.path!!.drop(1) else uri.path!!
    }
}