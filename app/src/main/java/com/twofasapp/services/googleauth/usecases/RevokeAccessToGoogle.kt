package com.twofasapp.services.googleauth.usecases

import android.content.Context
import com.twofasapp.base.usecase.UseCase
import com.twofasapp.services.googleauth.GoogleAuthService
import com.twofasapp.services.googleauth.models.GoogleAuthResult
import io.reactivex.Scheduler
import io.reactivex.Single

class RevokeAccessToGoogle(
    private val context: Context,
    private val googleAuthService: GoogleAuthService,
) : UseCase<Single<GoogleAuthResult>> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<GoogleAuthResult> =
        googleAuthService.revokeAccess(context)
}