package com.twofasapp.backup.ui.export

import android.net.Uri
import com.twofasapp.backup.data.FilesProvider
import com.twofasapp.data.session.SessionRepository
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExportBackupPresenter(
    private val view: ExportBackupContract.View,
    private val navigator: ScopedNavigator,
    private val checkLockStatus: CheckLockStatus,
    private val exportBackup: ExportBackup,
    private val filesProvider: FilesProvider,
    private val sessionRepository: SessionRepository,
) : ExportBackupContract.Presenter() {

    private var backupPassword: String = ""
    private var exportType = ExportType.Download

    private enum class ExportType { Download, Share }

    override fun onViewAttached() {
        view.toolbarBackClicks().safelySubscribe { navigator.navigateBack() }
        view.exportClicks().safelySubscribe {
            exportType = ExportType.Download
            startExport(it)
        }
        view.shareClicks().safelySubscribe {
            exportType = ExportType.Share
            startExport(it)
        }
        view.passwordClicks().safelySubscribe { view.setPasswordSwitchChecked(it.not()) }
    }

    override fun onExportAuthenticated(isExportWithoutPasswordChecked: Boolean) {
        if (isExportWithoutPasswordChecked) {
            showPicker()
        } else {
            view.showPasswordDialog()
        }
    }

    override fun onFilePicked(fileUri: Uri?, isExportWithoutPasswordChecked: Boolean) {
        fileUri?.let {
            exportBackup.execute(it, if (isExportWithoutPasswordChecked) null else backupPassword).subscribe({ result ->
                when (result) {
                    is ExportBackup.Result.Success -> {
                        sessionRepository.resetBackupReminder()
                        view.showResultToast(com.twofasapp.resources.R.string.backup__export_result_success)
                        navigator.finish()
                    }

                    ExportBackup.Result.UnknownError -> {
                        view.showResultToast(com.twofasapp.resources.R.string.commons__unknown_error)
                    }
                }
            }, { it.printStackTrace() }).addToDisposables()
        }
    }

    override fun onPasswordDialogCanceled() {
        backupPassword = ""
        view.setPasswordSwitchChecked(false)
    }

    override fun onPasswordDialogSaved(password: String) {
        backupPassword = password
        showPicker()
    }

    private fun startExport(isExportWithoutPasswordChecked: Boolean) {
        when (checkLockStatus.execute()) {
            LockMethodEntity.NO_LOCK -> onExportAuthenticated(isExportWithoutPasswordChecked)
            LockMethodEntity.PIN_LOCK, LockMethodEntity.PIN_SECURED -> navigator.openAuthenticate(true)

            LockMethodEntity.FINGERPRINT_LOCK, LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED -> navigator.openAuthenticate(true)
        }
    }

    private fun showPicker() {
        when (exportType) {
            ExportType.Download -> view.showSaveFilePicker(generateFilename())
            ExportType.Share -> exportBackup.execute(null, backupPassword).safelySubscribe {
                backupPassword = ""

                when (it) {
                    is ExportBackup.Result.Success -> view.showSharePicker(
                        dirPath = filesProvider.getExternalTmpPath(),
                        dir = "backup",
                        filename = generateFilename(),
                        content = it.backupContent,
                    )

                    ExportBackup.Result.UnknownError -> view.showResultToast(com.twofasapp.resources.R.string.backup__share_result_failure)
                }
            }
        }
    }

    private fun generateFilename() = "2fas-backup-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.2fas"
}