package com.twofasapp.features.navigator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import com.twofasapp.backup.ui.export.ExportBackupActivity
import com.twofasapp.core.RequestCodes
import com.twofasapp.developer.ui.DeveloperActivity
import com.twofasapp.extensions.hideSoftKeyboard
import com.twofasapp.extensions.startActivity
import com.twofasapp.extensions.startActivityForResult
import com.twofasapp.extensions.toastLong
import com.twofasapp.features.addserviceqr.AddServiceQrActivity
import com.twofasapp.features.backup.BackupActivity
import com.twofasapp.features.backup.import.ImportBackupActivity
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.resources.R
import com.twofasapp.security.ui.lock.LockActivity
import com.twofasapp.ui.main.MainActivity


class ActivityScopedNavigator(
    private val activity: Activity,
    private val checkLockStatus: CheckLockStatus,
) : ScopedNavigator {

    override fun requireActivity(): Activity {
        return activity
    }

    override fun navigateBack() {
        activity.onBackPressed()
    }

    override fun hideKeyboard() {
        activity.hideSoftKeyboard()
    }

    override fun finish() {
        activity.finish()
    }

    override fun finishResultOk(params: Map<String, Any>) {
        if (params.isNotEmpty()) {
            val returnIntent = Intent()
            returnIntent.putExtras(bundleOf(*params.map { Pair(it.key, it.value) }.toTypedArray()))
            activity.setResult(Activity.RESULT_OK, returnIntent)

        } else {
            activity.setResult(Activity.RESULT_OK)
        }

        activity.finish()
    }

    override fun openMain() {
        activity.startActivity<MainActivity>()
    }

    override fun openGooglePlay() {
        val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.twofasapp"))
        resolveIntent(storeIntent) { activity.startActivity(storeIntent) }
    }

    override fun openShowService(service: ServiceDto, showEditIcon: Boolean) {
//        activity.startActivityForResult<ServiceActivity>(
//            ServiceActivity.REQUEST_KEY_ADD_SERVICE,
//            ServiceActivity.ARG_SERVICE to service,
//            ServiceActivity.ARG_SERVICE_ID to service.id,
//            ServiceActivity.ARG_SHOW_ICON_PICKER to showEditIcon,
//        )
    }


    override fun openSecurityWithAuth() {
//        when (checkLockStatus.execute()) {
//            LockMethodEntity.NO_LOCK -> openSecurity()
//            else -> activity.startActivityForResult<LockActivity>(requestKey = MainServicesActivity.REQUEST_KEY_OPEN_SECURITY, "canGoBack" to true)
//        }
    }

    override fun openAuthenticate(canGoBack: Boolean, requestCode: Int?) {
        when (checkLockStatus.execute()) {
            LockMethodEntity.NO_LOCK -> Unit
            else -> activity.startActivityForResult<LockActivity>(
                requestCode ?: RequestCodes.AUTH_REQUEST_CODE, "canGoBack" to canGoBack
            )
        }
    }

    override fun openBackup(isOpenedFromBackupNotice: Boolean) {
        activity.startActivity<BackupActivity>(BackupActivity.EXTRA_IS_OPENED_FROM_BACKUP_NOTICE to isOpenedFromBackupNotice)
    }

    override fun openAddServiceQr() {
        activity.startActivityForResult<AddServiceQrActivity>(AddServiceQrActivity.REQUEST_CODE)
    }

    override fun openDeveloperOptions() {
        activity.startActivity<DeveloperActivity>()
    }

    override fun openExportBackup() {
        activity.startActivity<ExportBackupActivity>()
    }

    override fun openImportBackup() {
        activity.startActivity<ImportBackupActivity>()
    }

    private fun resolveIntent(intent: Intent, action: () -> Unit) {
        try {
            action.invoke()
        } catch (e: Exception) {
            activity.toastLong(R.string.errors__no_app)
        }
    }
}