package com.twofasapp.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.feature.security.ui.lock.LockActivity

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

private inline fun <reified T : Any> Context.intentForLegacy(vararg params: Pair<String, Any?>): Intent =
    Intent(this, T::class.java).apply {
        putExtras(bundleOf(*params))
    }

private inline fun <reified T : Activity> Activity.startActivityForResult(requestKey: Int, vararg params: Pair<String, Any?>) =
    startActivityForResult(intentForLegacy<T>(*params), requestKey)


object RequestCodes {
    const val AUTH_REQUEST_CODE = 12022
}