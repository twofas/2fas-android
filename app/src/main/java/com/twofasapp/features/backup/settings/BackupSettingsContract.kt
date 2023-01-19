package com.twofasapp.features.backup.settings

import com.mikepenz.fastadapter.IItem
import com.twofasapp.base.BasePresenter

internal interface BackupSettingsContract {

    interface View {
        fun updateItems(items: List<IItem<*>>)
        fun showSetPasswordDialog()
        fun showRemovePasswordDialog(showError: Boolean)
        fun showWipePasswordDialog(showError: Boolean)
        fun showWipeConfirmDialog(confirmAction: () -> Unit)
        fun showErrorDialog(titleRes: Int? = null, msgRes: Int? = null, title: String? = null, msg: String? = null)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun onPasswordDialogSaved(password: String)
        abstract fun onPasswordDialogCanceled()
        abstract fun onRemovePasswordEntered(password: String)
        abstract fun onWipePasswordEntered(password: String)
    }
}