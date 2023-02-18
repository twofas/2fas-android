package com.twofasapp.browserextension.ui.main.permission

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.twofasapp.design.compose.dialogs.RationaleDialog


@Composable
internal fun RequestPermission(
    permission: String,
    rationaleTitle: String = "",
    rationaleText: String = "",
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var showRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            showRationale = true
        }
    }

    val permissionCheckResult = ContextCompat.checkSelfPermission(LocalContext.current, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        onGranted()
    } else {
        LaunchedEffect(Unit) {
            launcher.launch(permission)
        }
    }

    if (showRationale) {
        RationaleDialog(
            title = rationaleTitle, text = rationaleText, onDismiss = onDismiss
        )
    }
}