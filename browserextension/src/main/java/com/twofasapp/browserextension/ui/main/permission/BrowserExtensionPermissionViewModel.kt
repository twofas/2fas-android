package com.twofasapp.browserextension.ui.main.permission

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.permissions.NotificationsPermissionRequestFlow
import com.twofasapp.permissions.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BrowserExtensionPermissionViewModel(
    private val notificationsPermissionRequestFlow: NotificationsPermissionRequestFlow,
) : BaseViewModel() {

    val permissionStatus = MutableStateFlow<PermissionStatus?>(null)

    fun askForPermission() {
        viewModelScope.launch {
            notificationsPermissionRequestFlow.execute()
                .take(1)
                .collect { status ->
                    println("dupa: $status")
                    permissionStatus.update { status }
                }
        }
    }
}
