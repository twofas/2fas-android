package com.twofasapp.features.backup

import android.content.Intent
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.services.googleauth.usecases.HandleSignInToGoogleResult

internal class BackupPresenter(
    private val view: BackupContract.View,
    private val navigator: ScopedNavigator,
    private val handleSignInToGoogleResult: HandleSignInToGoogleResult,
) : BackupContract.Presenter() {


    override fun onViewAttached() {
        view.toolbarBackClicks()
            .safelySubscribe { navigator.navigateBack() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        handleSignInToGoogleResult.execute(HandleSignInToGoogleResult.Params(requestCode, resultCode, data))
    }
}
