package com.twofasapp.data.cloud.googleauth

import android.accounts.Account

data class AccountCredentials(
    val email: String,
    val account: Account
)