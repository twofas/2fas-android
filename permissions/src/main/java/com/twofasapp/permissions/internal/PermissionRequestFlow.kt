package com.twofasapp.permissions.internal

import android.app.Activity
import com.twofasapp.permissions.PermissionStatus
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class PermissionRequestFlow(activity: Activity) : KoinComponent {

    private val flowPermission: FlowPermission by inject { parametersOf(activity) }

    protected abstract val permission: String

    fun execute(): Flow<PermissionStatus> {
        return flowPermission.requestPermission(permission)
    }
}