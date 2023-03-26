package com.twofasapp.features.backup

import android.content.Intent
import io.reactivex.Flowable

internal interface BackupContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }
}