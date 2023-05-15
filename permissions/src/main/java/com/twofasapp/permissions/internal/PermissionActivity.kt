package com.twofasapp.permissions.internal

import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.FragmentActivity
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.permissions.PermissionStatus
import org.koin.android.ext.android.inject

internal class PermissionActivity : FragmentActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val REQUEST_CODE = 6712
        const val PERMISSION_KEY = "permission"
    }

    private val permissionProcessor: PermissionProcessor by inject()
    private val permissionSharedFlow: PermissionSharedFlow by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeWindowSecure(allow = true)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val permission = intent.getStringExtra(PERMISSION_KEY)
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val grantedPermissions = ArrayList<String>()
            val deniedPermissions = ArrayList<String>()
            for (i in permissions.indices) {
                val permission = permissions[i]
                when (grantResults[i]) {
                    PermissionChecker.PERMISSION_DENIED,
                    PermissionChecker.PERMISSION_DENIED_APP_OP -> deniedPermissions.add(permission)
                    PermissionChecker.PERMISSION_GRANTED -> grantedPermissions.add(permission)
                }
            }
            val needsRationale = deniedPermissions.any { p -> ActivityCompat.shouldShowRequestPermissionRationale(this, p) }
            val doNotAskAgain = deniedPermissions.isNotEmpty() && !needsRationale

            when {
                permissions.isEmpty() -> {
                    permissionProcessor.publisher.offer(PermissionStatus.DENIED)
                    permissionSharedFlow.flow.tryEmit(PermissionStatus.DENIED)
                }
                deniedPermissions.isEmpty() -> {
                    permissionProcessor.publisher.offer(PermissionStatus.GRANTED)
                    permissionSharedFlow.flow.tryEmit(PermissionStatus.GRANTED)
                }
                needsRationale -> {
                    permissionProcessor.publisher.offer(PermissionStatus.DENIED)
                    permissionSharedFlow.flow.tryEmit(PermissionStatus.DENIED)
                }
                doNotAskAgain -> {
                    permissionProcessor.publisher.offer(PermissionStatus.DENIED_NEVER_ASK)
                    permissionSharedFlow.flow.tryEmit(PermissionStatus.DENIED_NEVER_ASK)
                }
                else -> {
                    permissionProcessor.publisher.offer(PermissionStatus.DENIED)
                    permissionSharedFlow.flow.tryEmit(PermissionStatus.DENIED)
                }
            }

            finish()
        }
    }
}