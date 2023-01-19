package com.twofasapp.permissions.internal

import android.app.Activity
import com.twofasapp.permissions.PermissionStatus
import io.reactivex.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class PermissionRequest(activity: Activity) : KoinComponent {

    private val rxPermission: RxPermission by inject { parametersOf(activity) }

    protected abstract val permission: String

    fun execute(): Single<PermissionStatus> {
        return rxPermission.requestPermission(permission)
    }
}