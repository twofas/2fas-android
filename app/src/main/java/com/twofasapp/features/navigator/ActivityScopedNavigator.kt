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

    override fun openAuthenticate(canGoBack: Boolean, requestCode: Int?) {
        when (checkLockStatus.execute()) {
            LockMethodEntity.NO_LOCK -> Unit
            else -> activity.startActivityForResult<LockActivity>(
                requestCode ?: RequestCodes.AUTH_REQUEST_CODE, "canGoBack" to canGoBack
            )
        }
    }
}