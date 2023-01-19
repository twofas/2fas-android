package com.twofasapp.permissions.internal

import android.app.Activity
import com.twofasapp.extensions.startActivity
import com.twofasapp.permissions.PermissionStatus
import io.reactivex.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class RxPermission(
    private val activity: Activity,
) : KoinComponent {

    private val permissionProcessor: PermissionProcessor by inject()

    fun requestPermission(permission: String): Single<PermissionStatus> =
        permissionProcessor.publisher
            .doOnSubscribe { activity.startActivity<PermissionActivity>(PermissionActivity.PERMISSION_KEY to permission) }
            .firstOrError()
}