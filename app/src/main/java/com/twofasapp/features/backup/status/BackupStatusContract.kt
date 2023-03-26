package com.twofasapp.features.backup.status

import com.mikepenz.fastadapter.IItem

internal interface BackupStatusContract {

    interface View {
        fun isOpenedFromBackupNotice(): Boolean

        fun updateItems(items: List<IItem<*>>)
        fun showSyncSettings()
        fun showTurnOffDialog(confirmAction: () -> Unit)
        fun showErrorDialog(titleRes: Int? = null, msgRes: Int? = null, title: String? = null, msg: String? = null)
        fun showBackupFileDialog(text: String)
        fun showEnterPasswordDialog(showError: Boolean, signOutOnCancel: Boolean, action: (String) -> Unit)
    }

    abstract class Presenter : com.twofasapp.base.BasePresenter() {
        abstract fun signOut()
    }
}