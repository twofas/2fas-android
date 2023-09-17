package com.twofasapp.features.backup

import io.reactivex.Flowable

internal interface BackupContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
    }
}