package com.twofasapp.permissions

import android.Manifest
import android.app.Activity
import com.twofasapp.permissions.internal.PermissionRequest
import com.twofasapp.permissions.internal.PermissionRequestFlow

class CameraPermissionRequestFlow(activity: Activity) : PermissionRequestFlow(activity) {

    override val permission: String = Manifest.permission.CAMERA
}