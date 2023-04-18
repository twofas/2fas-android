package com.twofasapp.services.googleauth.usecases

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.twofasapp.base.usecase.UseCase
import com.twofasapp.services.googleauth.models.AccountCredentials
import io.reactivex.Scheduler

class GetAccountCredentials(
    private val context: Context
) : UseCase<AccountCredentials?> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler) =
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            AccountCredentials(
                email = it.email!!,
                account = it.account!!
            )
        }
}