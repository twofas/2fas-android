package com.twofasapp.backup.ui.export

import android.net.Uri
import io.reactivex.Flowable

interface ExportBackupContract {

    interface View {
        fun toolbarBackClicks(): Flowable<Unit>
        fun exportClicks(): Flowable<Boolean>
        fun shareClicks(): Flowable<Boolean>
        fun passwordClicks(): Flowable<Boolean>

        fun showSaveFilePicker(filename: String)
        fun showResultToast(res: Int)
        fun showPasswordDialog()
        fun setPasswordSwitchChecked(isChecked: Boolean)
        fun showSharePicker(dirPath: String, dir: String, filename: String, content: String)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun onExportAuthenticated(isExportWithoutPasswordChecked: Boolean)
        abstract fun onFilePicked(fileUri: Uri?, isExportWithoutPasswordChecked: Boolean)
        abstract fun onPasswordDialogCanceled()
        abstract fun onPasswordDialogSaved(password: String)
    }
}