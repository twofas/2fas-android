package com.twofasapp.features.backup.import

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.databinding.ActivityImportBackupBinding
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.design.dialogs.SimpleInputDialog
import com.twofasapp.extensions.clicksThrottled
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeVisible
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.extensions.toastLong
import org.koin.android.ext.android.inject

class ImportBackupActivity : BaseActivityPresenter<ActivityImportBackupBinding>(), ImportBackupContract.View {

    companion object {
        private const val IMPORT_FILE_PICKER = 23142
    }

    private val presenter: ImportBackupContract.Presenter by injectThis()
    private val analyticsService: com.twofasapp.core.analytics.AnalyticsService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityImportBackupBinding::inflate)
        setPresenter(presenter)

        presenter.handleIncomingData(intent.data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMPORT_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            presenter.onFilePicked(data?.data)
        } else {
            finish()
        }
    }

    override fun showResultToast(text: Int) = toastLong(text)

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun importClicks() = viewBinding.confirmImport.clicksThrottled()

    override fun cancelClicks() = viewBinding.cancel.clicksThrottled()

    override fun cancelErrorClicks() = viewBinding.cancelError.clicksThrottled()

    override fun chooseAnotherFileClicks() = viewBinding.chooseAnotherFile.clicksThrottled()

    override fun showContent(numberOfServices: Int, isPasswordProtected: Boolean) {
        viewBinding.progress.makeGone()
        viewBinding.content.makeVisible()
        viewBinding.error.makeGone()

        if (isPasswordProtected) {
            viewBinding.numberOfServices.makeGone()
            viewBinding.description.text = getString(com.twofasapp.resources.R.string.import_backup_msg1_encrypted)

        } else {
            viewBinding.numberOfServices.makeVisible()
            viewBinding.description.text = getString(com.twofasapp.resources.R.string.import_backup_msg1)
            viewBinding.numberOfServices.text = if (numberOfServices == 1) "1" else "$numberOfServices"
        }
    }

    override fun showError(msg: Int) {
        viewBinding.progress.makeGone()
        viewBinding.content.makeGone()
        viewBinding.error.makeVisible()
        viewBinding.descriptionError.text = getString(msg)
    }

    override fun showProgress() {
        viewBinding.progress.makeVisible()
        viewBinding.content.makeGone()
        viewBinding.error.makeGone()
    }

    override fun showFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

        try {
            startActivityForResult(intent, IMPORT_FILE_PICKER)
            return
        } catch (e: Exception) {
            analyticsService.captureException(e)
        }

        intent.action = Intent.ACTION_GET_CONTENT

        try {
            startActivityForResult(intent, IMPORT_FILE_PICKER)
            return
        } catch (e: ActivityNotFoundException) {
            analyticsService.captureException(e)
            InfoDialog(
                context = this,
                title = "Error",
                msg = "Could not find system file provider.\n\nIf you removed default documents application you need to restore it in order to make the import work."
            ).show()
        } catch (e: Exception) {
            analyticsService.captureException(e)
            toastLong("System error! Could not launch file provider!")
        }
    }

    override fun showPasswordDialog(onConfirmed: (String) -> Unit) {
        SimpleInputDialog(this).show(
            title = getString(com.twofasapp.resources.R.string.backup__enter_password_dialog_title),
            description = getString(com.twofasapp.resources.R.string.backup__enter_password_title),
            okText = getString(com.twofasapp.resources.R.string.commons__continue),
            hint = getString(com.twofasapp.resources.R.string.backup__password),
            allowEmpty = false,
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
            isPassword = true,
        ) { onConfirmed.invoke(it) }
    }

    override fun showWrongPasswordDialog(onConfirmed: (String) -> Unit) {
        SimpleInputDialog(this).show(
            title = getString(com.twofasapp.resources.R.string.backup__enter_password_dialog_title),
            description = getString(com.twofasapp.resources.R.string.backup__enter_password_title),
            okText = getString(com.twofasapp.resources.R.string.commons__continue),
            hint = getString(com.twofasapp.resources.R.string.backup__password),
            allowEmpty = false,
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
            isPassword = true,
            errorText = getString(com.twofasapp.resources.R.string.backup__incorrect_password)
        ) { onConfirmed.invoke(it) }
    }
}