package com.twofasapp.permissions

import android.Manifest
import android.app.Activity
import com.twofasapp.permissions.internal.PermissionRequest

class CameraPermissionRequest(activity: Activity) : PermissionRequest(activity) {

    override val permission: String = Manifest.permission.CAMERA
}