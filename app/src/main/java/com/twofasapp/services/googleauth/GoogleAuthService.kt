package com.twofasapp.services.googleauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.twofasapp.services.googleauth.models.GoogleAuthResult
import io.reactivex.Single

interface GoogleAuthService {
    fun signIn(activity: Activity): Single<GoogleAuthResult>
    fun signOut(context: Context): Single<GoogleAuthResult>
    fun revokeAccess(context: Context): Single<GoogleAuthResult>
    fun handleSignInResult(requestCode: Int, resultCode: Int, resultData: Intent?)
}