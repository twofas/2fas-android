package com.twofasapp.features.backup.settings

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.twofasapp.backup.ui.ExportBackupPasswordDialog
import com.twofasapp.base.BaseFragmentPresenter
import com.twofasapp.databinding.FragmentBackupSettingsBinding
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.design.dialogs.SimpleInputDialog
import com.twofasapp.views.ModelDiffUtilCallback

class BackupSettingsFragment : BaseFragmentPresenter<FragmentBackupSettingsBinding>(), BackupSettingsContract.View,
    ExportBackupPasswordDialog.Listener {

    interface Listener {
        fun updateToolbar(title: String)
    }

    companion object {
        fun newInstance() = BackupSettingsFragment()
    }

    private val presenter: BackupSettingsContract.Presenter by injectThis()
    private val adapter = FastItemAdapter<IItem<*>>()

    private val confirmWipeDialog: MaterialDialog by lazy {
        MaterialDialog(requireActivity())
            .cancelable(true)
            .customView(view = WipeBackupConfirmation(requireContext()), noVerticalPadding = true, scrollable = true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState, FragmentBackupSettingsBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPresenter(presenter)
        (requireActivity() as Listener).updateToolbar(getString(com.twofasapp.resources.R.string.backup__synchronization_settings))

        viewBinding.recycler.adapter = adapter
        viewBinding.recycler.itemAnimator = null
    }

    override fun updateItems(items: List<IItem<*>>) {
        FastAdapterDiffUtil.set(adapter.itemAdapter, items, ModelDiffUtilCallback())
    }

    override fun showSetPasswordDialog() {
        ExportBackupPasswordDialog(requireContext(), this).show()
    }

    override fun showRemovePasswordDialog(showError: Boolean) {
        SimpleInputDialog(requireContext()).show(
            title = getString(com.twofasapp.resources.R.string.backup__enter_password_dialog_title),
            description = getString(com.twofasapp.resources.R.string.backup__remove_password_msg),
            okText = getString(com.twofasapp.resources.R.string.commons__continue),
            hint = getString(com.twofasapp.resources.R.string.backup__password),
            allowEmpty = false,
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
            isPassword = true,
            errorText = if (showError) getString(com.twofasapp.resources.R.string.backup__incorrect_password) else null
        ) {
            presenter.onRemovePasswordEntered(it)
        }
    }

    override fun showWipePasswordDialog(showError: Boolean) {
        SimpleInputDialog(requireContext()).show(
            title = getString(com.twofasapp.resources.R.string.backup__enter_password_dialog_title),
            description = getString(com.twofasapp.resources.R.string.backup__revoke_google_access_msg),
            okText = getString(com.twofasapp.resources.R.string.commons__continue),
            hint = getString(com.twofasapp.resources.R.string.backup__password),
            allowEmpty = false,
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
            isPassword = true,
            errorText = if (showError) getString(com.twofasapp.resources.R.string.backup__incorrect_password) else null
        ) {
            presenter.onWipePasswordEntered(it)
        }
    }

    override fun showWipeConfirmDialog(confirmAction: () -> Unit) {
        (confirmWipeDialog.getCustomView() as WipeBackupConfirmation).setup(
            confirmAction = {
                confirmAction.invoke()
                confirmWipeDialog.dismiss()
            },
            cancelAction = confirmWipeDialog::dismiss,
            closeAction = confirmWipeDialog::dismiss,
        )

        confirmWipeDialog.show()
    }

    override fun showErrorDialog(titleRes: Int?, msgRes: Int?, title: String?, msg: String?) {
        InfoDialog(
            requireActivity(),
            title = titleRes?.let { getString(it) } ?: title,
            msg = msgRes?.let { getString(it) } ?: msg,
        ).show()
    }

    override fun onPasswordDialogSaved(password: String) {
        presenter.onPasswordDialogSaved(password)
    }

    override fun onPasswordDialogCanceled() {
        presenter.onPasswordDialogCanceled()
    }

    override fun getStringRes(res: Int): String {
        return getString(res)
    }
}
