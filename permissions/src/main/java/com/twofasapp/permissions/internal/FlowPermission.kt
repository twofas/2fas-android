package com.twofasapp.permissions.internal

import android.app.Activity
import com.twofasapp.extensions.startActivity
import com.twofasapp.permissions.PermissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class FlowPermission(
    private val activity: Activity,
) : KoinComponent {

    private val permissionSharedFlow: PermissionSharedFlow by inject()

    fun requestPermission(permission: String): Flow<PermissionStatus> {
        return permissionSharedFlow.flow
            .onSubscription {
                activity.startActivity<PermissionActivity>(PermissionActivity.PERMISSION_KEY to permission)
            }
    }
}