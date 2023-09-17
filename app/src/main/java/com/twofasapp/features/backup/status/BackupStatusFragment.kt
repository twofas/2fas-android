package com.twofasapp.features.backup.status

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.twofasapp.base.BaseFragmentPresenter
import com.twofasapp.databinding.FragmentBackupStatusBinding
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.design.dialogs.SimpleInputDialog
import com.twofasapp.views.ModelDiffUtilCallback

class BackupStatusFragment : BaseFragmentPresenter<FragmentBackupStatusBinding>(), BackupStatusContract.View {

    interface Listener {
        fun updateToolbar(title: String)
        fun onSettingsClick()
    }

    companion object {
        private const val ARG_IS_OPENED_FROM_BACKUP_NOTICE = "isOpenedFromBackupNotice"
        fun newInstance(isOpenedFromBackupNotice: Boolean) = BackupStatusFragment().apply {
            arguments = bundleOf(ARG_IS_OPENED_FROM_BACKUP_NOTICE to isOpenedFromBackupNotice)
        }
    }

    private val presenter: BackupStatusContract.Presenter by injectThis()
    private val listener: Listener by lazy { requireActivity() as Listener }
    private val adapter = FastItemAdapter<IItem<*>>()
    private val passwordDialog by lazy { SimpleInputDialog(requireContext()) }
    private var isPasswordDialogShown = false
        set(value) {
            field = value
        }

    private val backupFileDialog: MaterialDialog by lazy {
        MaterialDialog(requireActivity())
            .cancelable(true)
            .customView(view = ShowBackupFileView(requireContext()), scrollable = true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState, FragmentBackupStatusBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPresenter(presenter)
        listener.updateToolbar(getString(com.twofasapp.resources.R.string.backup__2fas_backup))

        viewBinding.recycler.adapter = adapter
        viewBinding.recycler.itemAnimator = null
    }

    override fun isOpenedFromBackupNotice() = requireArguments().getBoolean(ARG_IS_OPENED_FROM_BACKUP_NOTICE)

    override fun updateItems(items: List<IItem<*>>) {
        FastAdapterDiffUtil.set(adapter.itemAdapter, items, ModelDiffUtilCallback())
    }

    override fun showSyncSettings() {
        listener.onSettingsClick()
    }

    override fun showTurnOffDialog(confirmAction: () -> Unit) {
//        (confirmTurnOffDialog.getCustomView() as TurnOffBackupConfirmation).setup(
//            confirmAction = {
//                confirmAction.invoke()
//                confirmTurnOffDialog.dismiss()
//            },
//            cancelAction = confirmTurnOffDialog::dismiss,
//            closeAction = confirmTurnOffDialog::dismiss,
//        )
//
//        confirmTurnOffDialog.show()
    }

    override fun showBackupFileDialog(text: String) {
        (backupFileDialog.getCustomView() as ShowBackupFileView).setContent(text)
        backupFileDialog.show()
    }

    override fun showErrorDialog(titleRes: Int?, msgRes: Int?, title: String?, msg: String?) {
        InfoDialog(
            requireActivity(),
            title = titleRes?.let { getString(it) } ?: title,
            msg = msgRes?.let { getString(it) } ?: msg,
        ).show()
    }

    override fun showEnterPasswordDialog(showError: Boolean, signOutOnCancel: Boolean, action: (String) -> Unit) {
        if (isPasswordDialogShown && showError.not()) return

        isPasswordDialogShown = true
        passwordDialog.dismiss()
        passwordDialog.show(
            title = getString(com.twofasapp.resources.R.string.backup__enter_password_dialog_title),
            description = getString(com.twofasapp.resources.R.string.backup__backup_file_password_title),
            okText = getString(com.twofasapp.resources.R.string.commons__continue),
            hint = getString(com.twofasapp.resources.R.string.backup__password),
            isCancelable = false,
            allowEmpty = false,
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
            isPassword = true,
            errorText = if (showError) getString(com.twofasapp.resources.R.string.backup__incorrect_password) else null,
            cancelAction = {
                isPasswordDialogShown = false
                if (signOutOnCancel) {
                    presenter.signOut()
                }
            },
            okAction = {
                isPasswordDialogShown = false
                action.invoke(it)
            },
        )
    }
}
