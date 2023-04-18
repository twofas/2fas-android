package com.twofasapp.prefs

import android.app.Activity
import com.twofasapp.prefs.model.ServiceDto

interface ScopedNavigator {
    fun requireActivity(): Activity
    fun navigateBack()
    fun hideKeyboard()
    fun finish()
    fun finishResultOk(params: Map<String, Any> = emptyMap())

    fun openMain()
    fun openGooglePlay()
    fun openShowService(service: ServiceDto, showEditIcon: Boolean = false)
    fun openAuthenticate(canGoBack: Boolean = false, requestCode: Int? = null)
    fun openSecurityWithAuth()
    fun openBackup(isOpenedFromBackupNotice: Boolean = false)
    fun openAddServiceQr()
    fun openDeveloperOptions()
    fun openExportBackup()
    fun openImportBackup()
}