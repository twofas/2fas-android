package com.twofasapp.features.backup.import

import android.net.Uri
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.services.domain.ShowBackupNotice

class ImportBackupPresenter(
    private val view: ImportBackupContract.View,
    private val navigator: ScopedNavigator,
    private val importBackup: ImportBackup,
    private val showBackupNotice: ShowBackupNotice,
    private val syncBackupDispatcher: SyncBackupWorkDispatcher,
) : ImportBackupContract.Presenter() {

    private var fileUriCache: Uri? = null
    private var isFromDeeplink = false
    private var content: ImportBackupFromDisk.Content? = null

    override fun handleIncomingData(filePath: Uri?) {
        view.toolbarBackClicks().safelySubscribe { navigator.navigateBack() }
        view.importClicks().safelySubscribe { startImport() }
        view.cancelClicks().safelySubscribe { navigator.navigateBack() }
        view.cancelErrorClicks().safelySubscribe { navigator.navigateBack() }

        view.chooseAnotherFileClicks()
            .safelySubscribe {
                view.showProgress()
                view.showFilePicker()
            }

        view.showProgress()

        if (filePath != null) {
            isFromDeeplink = true
            onFilePicked(filePath)
        } else {
            view.showFilePicker()
        }
    }

    override fun onFilePicked(fileUri: Uri?) {
        fileUriCache = fileUri
        fileUri?.let { uri ->
            importBackup.read(uri)
                .safelySubscribe(
                    onSuccess = {
                        content = it
                        view.showContent(it.numberOfServices, it.isPasswordProtected)
                    },
                    onError = { handleError(it) })
        }
    }

    private fun startImport() {
        if (content?.isPasswordProtected == true) {
            view.showPasswordDialog { import(password = it) }
        } else {
            import(password = null)
        }
    }

    private fun import(password: String?) {
        fileUriCache?.let { uri ->
            view.showProgress()

            importBackup.import(uri, password)
                .safelySubscribe(
                    onSuccess = {
                        when (it) {
                            ImportBackup.Result.Success -> {
                                showBackupNotice.save(false)
                                syncBackupDispatcher.tryDispatch(SyncBackupTrigger.FIRST_CONNECT)
                                view.showResultToast(com.twofasapp.resources.R.string.import_ga_success)

                                if (isFromDeeplink) {
                                    navigator.openMain()
                                } else {
                                    navigator.finishResultOk()
                                }
                            }

                            ImportBackup.Result.WrongPasswordError -> {
                                view.showContent(content?.numberOfServices ?: 0, password.isNullOrBlank().not())
                                view.showWrongPasswordDialog { newPass -> import(password = newPass) }
                            }

                            ImportBackup.Result.UnknownError -> {
                                view.showContent(content?.numberOfServices ?: 0, password.isNullOrBlank().not())
                                view.showResultToast(com.twofasapp.resources.R.string.commons__unknown_error)
                            }
                        }
                    },
                    onError = {
                        it.printStackTrace()
                        handleError(it)
                    }
                )
        }
    }

    private fun handleError(exception: Throwable) {
        val msg = when (exception) {
            is FileTooBigException -> com.twofasapp.resources.R.string.backup__import_error_file_size
            else -> com.twofasapp.resources.R.string.backup__import_error_file_invalid
        }
        view.showError(msg)
    }
}