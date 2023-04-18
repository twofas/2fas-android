package com.twofasapp.services.googleauth.usecases

import android.content.Intent
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.services.googleauth.GoogleAuthService
import io.reactivex.Scheduler

class HandleSignInToGoogleResult(
    private val googleAuthService: GoogleAuthService,
) : UseCaseParameterized<HandleSignInToGoogleResult.Params, Unit> {

    data class Params(
        val requestCode: Int,
        val resultCode: Int,
        val resultData: Intent?
    )

    override fun execute(params: Params, subscribeScheduler: Scheduler, observeScheduler: Scheduler) =
        googleAuthService.handleSignInResult(
            requestCode = params.requestCode,
            resultCode = params.resultCode,
            resultData = params.resultData,
        )
}