package com.twofasapp.services.googleauth

import com.twofasapp.services.googleauth.usecases.HandleSignInToGoogleResult
import com.twofasapp.services.googleauth.usecases.RevokeAccessToGoogle
import com.twofasapp.services.googleauth.usecases.SignInToGoogle
import com.twofasapp.services.googleauth.usecases.SignOutFromGoogle
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val googleAuthModule = module {
    single<GoogleAuthService> { GoogleAuthServiceImpl() }
    single { SignInToGoogle(get()) }
    single { HandleSignInToGoogleResult(get()) }
    single { SignOutFromGoogle(androidContext(), get()) }
    single { RevokeAccessToGoogle(androidContext(), get()) }
}