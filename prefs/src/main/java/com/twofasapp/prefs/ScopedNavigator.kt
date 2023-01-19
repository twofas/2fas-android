package com.twofasapp.prefs

import android.app.Activity
import com.twofasapp.prefs.model.ServiceDto

interface ScopedNavigator {
    fun requireActivity(): Activity
    fun navigateBack()
    fun hideKeyboard()
    fun finish()
    fun finishResultOk(params: Map<String, Any> = emptyMap())
    fun restartApp()

    fun openMain()
    fun openGooglePlay()
    fun openShowService(service: ServiceDto, showEditIcon: Boolean = false)
    fun openDisposeService(service: ServiceDto)
    fun openAuthenticate(canGoBack: Boolean = false, requestCode: Int? = null)
    fun openSecurity()
    fun openSecurityWithAuth()
    fun openSettings()
    fun openExternalImport()
    fun openTrash()
    fun openBackup(isOpenedFromBackupNotice: Boolean = false)
    fun openAddServiceQr()
    fun openSupport()
    fun openAbout()
    fun openDeveloperOptions()
    fun openExportBackup()
    fun openImportBackup()
    fun openBrowserExtensionPairing()
    fun openNotifications()
}