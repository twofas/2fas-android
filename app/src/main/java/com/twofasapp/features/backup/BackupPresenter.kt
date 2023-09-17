package com.twofasapp.features.backup

import com.twofasapp.prefs.ScopedNavigator

internal class BackupPresenter(
    private val view: BackupContract.View,
    private val navigator: ScopedNavigator,
) : BackupContract.Presenter() {


    override fun onViewAttached() {
        view.toolbarBackClicks()
            .safelySubscribe { navigator.navigateBack() }
    }
}
