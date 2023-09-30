package com.twofasapp.data.cloud.googleauth

import android.content.Intent
import androidx.activity.result.ActivityResult

interface GoogleAuth {
    suspend fun createSignInIntent(): Intent
    suspend fun handleSignInResult(result: ActivityResult): SignInResult
    suspend fun signOut()
    suspend fun revokeAccess()
    fun accountCredentials(): AccountCredentials?
}