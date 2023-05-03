package com.twofasapp.backup.ui.export

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.core.content.FileProvider
import com.twofasapp.backup.databinding.ActivityExportBackupBinding
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.core.RequestCodes
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.extensions.clicksThrottled
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.extensions.toastLong
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

class ExportBackupActivity : BaseActivityPresenter<ActivityExportBackupBinding>(), ExportBackupContract.View, ExportBackupPasswordDialog.Listener {

    companion object {
        private const val EXPORT_FILE_PICKER = 48453
    }

    private val presenter: ExportBackupContract.Presenter by injectThis()
    private val analyticsService: com.twofasapp.core.analytics.AnalyticsService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeWindowSecure()
        setContentView(ActivityExportBackupBinding::inflate)
        setPresenter(presenter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodes.AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            presenter.onExportAuthenticated(isExportWithoutPasswordChecked())
        }

        if (requestCode == EXPORT_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            presenter.onFilePicked(data?.data, isExportWithoutPasswordChecked())
        }
    }

    override fun showResultToast(res: Int) = toastLong(res)

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun exportClicks() = viewBinding.export.clicksThrottled().map { isExportWithoutPasswordChecked() }

    override fun shareClicks() = viewBinding.share.clicksThrottled().map { isExportWithoutPasswordChecked() }

    override fun passwordClicks() = viewBinding.passwordLayout.clicksThrottled().map { isExportWithoutPasswordChecked() }

    override fun showSaveFilePicker(filename: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_TITLE, filename)
            }

        try {
            startActivityForResult(intent, EXPORT_FILE_PICKER)
        } catch (e: ActivityNotFoundException) {
            
            InfoDialog(
                context = this,
                title = "Error",
                msg = "Could not find system file provider.\n\nIf you removed default documents application you need to restore it in order to make the export work."
            ).show()
        } catch (e: Exception) {
            
            toastLong("System error! Could not launch file provider!")
        }
    }

    override fun showSharePicker(dirPath: String, dir: String, filename: String, content: String) {
        val backupDir = File(getExternalFilesDir(null), dir)

        if (backupDir != null) {
            backupDir.mkdir()
        }

        val file = File(backupDir, filename)
        val outputStream = FileOutputStream(file)
        outputStream.write(content.toByteArray())
        outputStream.close()

        val uri = FileProvider.getUriForFile(this, get<AppBuild>().id, file)

        val shareIntent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, filename)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                "2FAS Backup File",
            )
        )
    }

    override fun showPasswordDialog() {
        ExportBackupPasswordDialog(this, this).show()
    }

    override fun setPasswordSwitchChecked(isChecked: Boolean) {
        viewBinding.passwordSwitch.isChecked = isChecked
    }

    override fun onPasswordDialogSaved(password: String) {
        presenter.onPasswordDialogSaved(password)
    }

    override fun onPasswordDialogCanceled() {
        presenter.onPasswordDialogCanceled()
    }

    private fun isExportWithoutPasswordChecked() =
        viewBinding.passwordSwitch.isChecked
}