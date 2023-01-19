package com.twofasapp.services.googleauth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.twofasapp.services.googleauth.models.GoogleAuthResult
import com.twofasapp.services.googleauth.models.GoogleAuthResult.CancelReason
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

class GoogleAuthServiceImpl : GoogleAuthService {

    companion object {
        private const val REQUEST_CODE = 10234
        private val scope = Scope(DriveScopes.DRIVE_APPDATA)
    }

    private val publisher: PublishProcessor<GoogleAuthResult> = PublishProcessor.create()

    override fun signIn(activity: Activity): Single<GoogleAuthResult> =
        publisher
            .doOnSubscribe {
                val signInOptions = GoogleSignInOptions.Builder()
                    .requestEmail()
                    .requestScopes(scope)
                    .build()
                val client = GoogleSignIn.getClient(activity, signInOptions)
                activity.startActivityForResult(client.signInIntent, REQUEST_CODE)
            }
            .firstOrError()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())

    override fun signOut(context: Context): Single<GoogleAuthResult> =
        publisher
            .doOnSubscribe {
                GoogleSignIn.getClient(
                    context, GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestScopes(scope)
                        .build()
                )
                    .signOut()
                    .addOnSuccessListener { publish(GoogleAuthResult.Success()) }
                    .addOnCanceledListener { publish(GoogleAuthResult.Canceled()) }
                    .addOnFailureListener { publish(GoogleAuthResult.Failure(reason = it)) }
            }
            .firstOrError()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())

    override fun revokeAccess(context: Context): Single<GoogleAuthResult> =
        publisher
            .doOnSubscribe {
                GoogleSignIn.getClient(
                    context, GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestScopes(scope)
                        .build()
                )
                    .revokeAccess()
                    .addOnSuccessListener { publish(GoogleAuthResult.Success()) }
                    .addOnCanceledListener { publish(GoogleAuthResult.Canceled()) }
                    .addOnFailureListener { publish(GoogleAuthResult.Failure(reason = it)) }
            }
            .firstOrError()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())

    @SuppressLint("CheckResult")
    override fun handleSignInResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode != REQUEST_CODE) {
            publish(GoogleAuthResult.Canceled())
            return
        }

        if (resultCode != Activity.RESULT_OK) {
            publish(GoogleAuthResult.Canceled(reason = if (isCanceledDueToNetworkError(resultData)) CancelReason.NETWORK_ERROR else CancelReason.CANCELED))
            return
        }

        Completable.create {
            GoogleSignIn.getSignedInAccountFromIntent(resultData)
                .addOnSuccessListener {
                    if (it.grantedScopes.contains(scope)) {
                        publish(GoogleAuthResult.Success(email = it.email.orEmpty()))
                    } else {
                        publish(GoogleAuthResult.Canceled(CancelReason.PERMISSION_NOT_GRANTED))
                    }
                }
                .addOnCanceledListener { publish(GoogleAuthResult.Canceled(reason = if (isCanceledDueToNetworkError(resultData)) CancelReason.NETWORK_ERROR else CancelReason.CANCELED)) }
                .addOnFailureListener { publish(GoogleAuthResult.Failure(reason = it)) }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { it.printStackTrace() })
    }

    private fun publish(result: GoogleAuthResult) =
        publisher.offer(result)

    private fun isCanceledDueToNetworkError(resultData: Intent?) =
        try {
            resultData?.getParcelableExtra<com.google.android.gms.common.api.Status>("googleSignInStatus")?.statusCode == 7
        } catch (e: Exception) {
            false
        }
}