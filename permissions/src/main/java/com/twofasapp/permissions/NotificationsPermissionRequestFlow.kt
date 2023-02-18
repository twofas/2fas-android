package com.twofasapp.permissions

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.twofasapp.permissions.internal.PermissionRequest
import com.twofasapp.permissions.internal.PermissionRequestFlow

class NotificationsPermissionRequestFlow(activity: Activity) : PermissionRequestFlow(activity) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override val permission: String = Manifest.permission.POST_NOTIFICATIONS
}