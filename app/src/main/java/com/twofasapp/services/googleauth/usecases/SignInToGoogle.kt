package com.twofasapp.services.googleauth.usecases

import android.app.Activity
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.services.googleauth.GoogleAuthService
import com.twofasapp.services.googleauth.models.GoogleAuthResult
import io.reactivex.Scheduler
import io.reactivex.Single

class SignInToGoogle(
    private val googleAuthService: GoogleAuthService,
) : UseCaseParameterized<Activity, Single<GoogleAuthResult>> {

    override fun execute(params: Activity, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Single<GoogleAuthResult> =
        googleAuthService.signIn(params)
}