package com.twofasapp.features.backup.import

import android.net.Uri
import com.twofasapp.base.BasePresenter
import io.reactivex.Flowable

interface ImportBackupContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>
        fun importClicks(): Flowable<Unit>
        fun cancelClicks(): Flowable<Unit>
        fun cancelErrorClicks(): Flowable<Unit>
        fun chooseAnotherFileClicks(): Flowable<Unit>
        fun showFilePicker()
        fun showResultToast(text: String)
        fun showContent(numberOfServices: Int, isPasswordProtected: Boolean)
        fun showError(msg: String)
        fun showProgress()
        fun showPasswordDialog(onConfirmed: (String) -> Unit)
        fun showWrongPasswordDialog(onConfirmed: (String) -> Unit)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun handleIncomingData(filePath: Uri?)
        abstract fun onFilePicked(fileUri: Uri?)
    }
}