package com.twofasapp.designsystem.common

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
import com.twofasapp.designsystem.dialog.RationaleDialog

@Composable
fun RequestPermission(
    permission: String,
    rationaleEnabled: Boolean = true,
    rationaleTitle: String = "",
    rationaleText: String = "",
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    var showRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            showRationale = true
            onDenied()
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

    if (showRationale && rationaleEnabled) {
        RationaleDialog(
            title = rationaleTitle,
            text = rationaleText,
            onDismissRequest = onDismissRequest
        )
    }
}