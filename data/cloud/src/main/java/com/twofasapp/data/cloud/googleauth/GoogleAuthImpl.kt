package com.twofasapp.data.cloud.googleauth

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.resumeIfActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class GoogleAuthImpl(
    private val dispatchers: Dispatchers,
    private val context: Application,
) : GoogleAuth {

    companion object {
        private val scope = Scope(DriveScopes.DRIVE_APPDATA)
    }

    override suspend fun createSignInIntent(): Intent {
        return withContext(dispatchers.io) {
            val signInOptions = GoogleSignInOptions.Builder()
                .requestEmail()
                .requestScopes(scope)
                .build()

            val client = GoogleSignIn.getClient(context, signInOptions)

            client.signInIntent
        }
    }

    override suspend fun handleSignInResult(result: ActivityResult): SignInResult {
        return withContext(dispatchers.io) {

            suspendCancellableCoroutine { continuation ->

                if (result.resultCode != Activity.RESULT_OK) {
                    continuation.resumeIfActive(SignInResult.Canceled(reason = getCancelReason(result.data)))
                }

                GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .addOnSuccessListener {
                        if (it.grantedScopes.contains(scope)) {
                            continuation.resumeIfActive(SignInResult.Success(email = it.email.orEmpty()))
                        } else {
                            continuation.resumeIfActive(SignInResult.Canceled(SignInResult.CancelReason.PermissionNotGranted))
                        }
                    }
                    .addOnCanceledListener { continuation.resumeIfActive(SignInResult.Canceled(reason = getCancelReason(result.data))) }
                    .addOnFailureListener { continuation.resumeIfActive(SignInResult.Failure(reason = it)) }
            }
        }
    }

    override suspend fun signOut() {
        return withContext(dispatchers.io) {
            suspendCancellableCoroutine { continuation ->
                GoogleSignIn.getClient(
                    context, GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestScopes(scope)
                        .build()
                ).signOut()
                    .addOnSuccessListener { continuation.resumeIfActive(Unit) }
                    .addOnCanceledListener { continuation.resumeIfActive(Unit) }
                    .addOnFailureListener { continuation.resumeIfActive(Unit) }
            }
        }
    }

    override suspend fun revokeAccess() {
        return withContext(dispatchers.io) {
            suspendCancellableCoroutine { continuation ->
                GoogleSignIn.getClient(
                    context, GoogleSignInOptions.Builder()
                        .requestEmail()
                        .requestScopes(scope)
                        .build()
                ).revokeAccess()
                    .addOnSuccessListener { continuation.resumeIfActive(Unit) }
                    .addOnCanceledListener { continuation.resumeIfActive(Unit) }
                    .addOnFailureListener { continuation.resumeIfActive(Unit) }
            }
        }
    }

    override fun accountCredentials(): AccountCredentials? {
        return GoogleSignIn.getLastSignedInAccount(context)?.let {
            AccountCredentials(
                email = it.email!!,
                account = it.account!!,
            )
        }
    }

    private fun getCancelReason(resultData: Intent?) =
        try {
            //  Canceled due to network
            if (resultData?.getParcelableExtra<com.google.android.gms.common.api.Status>("googleSignInStatus")?.statusCode == 7) {
                SignInResult.CancelReason.NetworkError
            } else {
                SignInResult.CancelReason.Canceled
            }
        } catch (e: Exception) {
            SignInResult.CancelReason.Canceled
        }
}