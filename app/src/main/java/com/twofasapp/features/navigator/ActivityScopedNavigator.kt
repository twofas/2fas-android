package com.twofasapp.features.navigator

import android.app.Activity
import android.content.Intent
import androidx.core.os.bundleOf
import com.twofasapp.core.RequestCodes
import com.twofasapp.extensions.startActivity
import com.twofasapp.extensions.startActivityForResult
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
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

    override fun openAuthenticate(canGoBack: Boolean, requestCode: Int?) {
        when (checkLockStatus.execute()) {
            LockMethodEntity.NO_LOCK -> Unit
            else -> activity.startActivityForResult<LockActivity>(
                requestCode ?: RequestCodes.AUTH_REQUEST_CODE, "canGoBack" to canGoBack
            )
        }
    }
}