package com.twofasapp.usecases.totp

import android.net.Uri
import com.twofasapp.BuildConfig
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.entity.exceptions.InvalidOtpLinkFormat
import com.twofasapp.parsers.domain.OtpAuthLink
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber

class ParseOtpAuthLink : UseCaseParameterized<ParseOtpAuthLink.Params, Single<OtpAuthLink>> {

    private companion object {
        const val OTPAUTH = "otpauth"
        const val TOTP = "totp"
        const val HOTP = "hotp"
        const val SECRET = "secret"
        const val ISSUER = "issuer"
    }

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<OtpAuthLink> {
        return Single.create<OtpAuthLink> {
            try {

                if (BuildConfig.DEBUG) {
                    Timber.i("Parse link: ${params.link}")
                }

                val decoded = Uri.decode(params.link)
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
                    link = params.link,
                )

                it.onSuccess(otpAuthLink)


            } catch (e: Exception) {
                it.onError(InvalidOtpLinkFormat("${e.message}"))
            }
        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }


    private fun isUriValid(uri: Uri?) = uri?.scheme?.toLowerCase() == OTPAUTH

    private fun isAuthorityValid(uri: Uri) = uri.authority?.toLowerCase() == TOTP || uri.authority?.toLowerCase() == HOTP

    private fun mapQueryParams(uri: Uri) = uri.queryParameterNames.map { it to uri.getQueryParameter(it)!! }.toMap()

    private fun getPath(uri: Uri): String {
        if (uri.path == null) return ""

        return if (uri.path!!.startsWith("/")) uri.path!!.drop(1) else uri.path!!
    }

    data class Params(
        val link: String
    )
}