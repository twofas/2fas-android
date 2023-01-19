package com.twofasapp.services.googleauth.models

import android.accounts.Account

data class AccountCredentials(
    val email: String,
    val account: Account
)